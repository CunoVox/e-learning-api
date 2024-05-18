package com.elearning.controller;

import com.elearning.entities.ConfigProperty;
import com.elearning.entities.FileRelationship;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.FileDTO;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.models.wrapper.FileResponseWrapper;
import com.elearning.models.wrapper.ObjectResponseWrapper;
import com.elearning.models.wrapper.ResponseWrapper;
import com.elearning.reprositories.IConfigPropertyRepository;
import com.elearning.reprositories.IFileRelationshipRepository;
import com.elearning.utils.Constants;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.experimental.ExtensionMethod;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
@ExtensionMethod(Extensions.class)
public class FileRelationshipController extends BaseController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IFileRelationshipRepository fileRelationshipRepository;

    private String urlFileService;

    @Autowired
    private IConfigPropertyRepository configPropertyRepository;

    private FileDTO sendFileToService(MultipartFile fileToUpload, String type) {
        try {
            if (null != fileToUpload) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", fileToUpload.getResource());
                body.add("path", "public");
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
                messageConverters.add(new FormHttpMessageConverter());
                messageConverters.add(new StringHttpMessageConverter());
                messageConverters.add(new MappingJackson2HttpMessageConverter());

                ResponseEntity<FileResponseWrapper> response = restTemplate.exchange(
                        getUrlFileService() + "/api/file/upload?type=" + type,
                        HttpMethod.POST,
                        requestEntity,
                        FileResponseWrapper.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return Objects.requireNonNull(response.getBody()).getData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUrlFileService() {
        if (StringUtils.isBlankOrNull(urlFileService)) {
            ConfigProperty configProperty = configPropertyRepository.findByName("url_file_service");
            if (configProperty !=null ) {
                urlFileService= configProperty.getValue();
            }
        }
        return urlFileService;
    }

    public String getPathFile(String fileId, String parentType) {
        if (!parentType.isBlankOrNull() && !fileId.isBlankOrNull()) {
            switch (EnumParentFileType.valueOf(parentType)) {
                // Case 1 - Video
                case COURSE_VIDEO:
                    return Constants.BASE_VIDEO_URL + fileId;

                // Case 2 - Image
                case COURSE_IMAGE:
                case CATEGORY_IMAGE:
                case USER_AVATAR:
                case USER_PROFILE_DESCRIPTION:
                case COURSE_DESCRIPTION:
                    return Constants.BASE_IMAGE_URL + fileId;

                default:
                    return null;
            }
        }
        return null;
    }

    public List<FileRelationshipDTO> getFileRelationships(List<String> parentIds, String type) {
        List<FileRelationship> fileRelationships = fileRelationshipRepository.findAllByParentIdInAndParentType(parentIds, type);
        return toDTOS(fileRelationships);
    }

    public Map<String, List<FileRelationshipDTO>> mapFileRelationships(List<String> parentIds, String type) {
        List<FileRelationshipDTO> fileRelationships = getFileRelationships(parentIds, type);
        Map<String, List<FileRelationshipDTO>> map = new HashMap<>();
        for (FileRelationshipDTO fileRelationship : fileRelationships) {
            if (map.containsKey(fileRelationship.getParentId())) {
                map.get(fileRelationship.getParentId()).add(fileRelationship);
            } else {
                List<FileRelationshipDTO> list = new ArrayList<>();
                list.add(fileRelationship);
                map.put(fileRelationship.getParentId(), list);
            }
        }
        return map;
    }

    public List<FileRelationshipDTO> getAllFile() {
        List<FileRelationship> fileRelationships = fileRelationshipRepository.findAll();
        return toDTOS(fileRelationships);
    }

    public Map<String, String> getUrlOfFile(List<FileRelationshipDTO> fileRelationshipDTOS) {
        Map<String, String> map = new HashMap<>();
        for (FileRelationshipDTO fileRelationshipDTO : fileRelationshipDTOS) {
            if (!fileRelationshipDTO.getPathFile().isBlankOrNull()) {
                map.put(fileRelationshipDTO.getParentId(), fileRelationshipDTO.getPathFile());
            }
        }
        return map;
    }

//    public void deleteFileToGoogleDrive(String fileId) throws Exception {
//        googleDrive.files().delete(fileId).execute();
//    }

    public void deleteFile(String id) {
        Optional<FileRelationship> fileRelationship = fileRelationshipRepository.findById(id);
        if (fileRelationship.isEmpty()) {
            throw new ServiceException("Không tìm thấy file trong hệ thống");
        }
//        try {
//            deleteFileToGoogleDrive(fileRelationship.get().getFileId());
//        } catch (Exception ignored){}
        fileRelationshipRepository.deleteById(id);
    }

    public void deleteFileByPathFile(String pathFile) {
        FileRelationship fileRelationship = fileRelationshipRepository.findByPathFile(pathFile);
        if (fileRelationship == null) {
            throw new ServiceException("Không tìm thấy file trong hệ thống");
        }
        fileRelationshipRepository.deleteByPathFile(pathFile);
    }

    public FileRelationshipDTO saveFile(MultipartFile multipartFile, String parentId, String parentType) {
        //Todo: chưa validate tồn tại parentId
//        validateUploadFile(parentId, parentType);
        String userId = this.getUserIdFromContext();
        FileDTO file = sendFileToService(multipartFile, parentType);
        if (file == null) {
            throw new ServiceException("Tải file lên không thành công");
        }
        FileRelationship fileRelationship = buildFileDriveToFileRelationship(file);
        fileRelationship.setParentId(parentId);
        fileRelationship.setParentType(parentType);
        fileRelationship.setPathFile(getPathFile(file.getId(), parentType));
        fileRelationship.setName(multipartFile.getOriginalFilename());
        fileRelationship.setDownloadLink(file.getWebContentLink());
        fileRelationship.setCreatedAt(new Date());
        fileRelationship.setCreatedBy(userId);
        FileRelationship fileRelationshipSaved = fileRelationshipRepository.save(fileRelationship);
        return toDTO(fileRelationshipSaved);
    }

    public FileRelationship buildFileDriveToFileRelationship(FileDTO fileDrive) {
        if (fileDrive == null) return new FileRelationship();
        return FileRelationship.builder()
                .fileId(fileDrive.getId() != null ? fileDrive.getId() : null)
                .name(fileDrive.getName() != null ? fileDrive.getName() : null)
                .size(fileDrive.getSize() != null ? Long.valueOf(fileDrive.getSize()) : null)
                .mimeType(fileDrive.getMimeType() != null ? fileDrive.getMimeType() : null)
                .webViewLink(fileDrive.getWebViewLink() != null ? fileDrive.getWebViewLink() : null)
//                .duration(fileDrive.getVideoMediaMetadata() != null
//                        && fileDrive.getVideoMediaMetadata().getDurationMillis() != null ? fileDrive.getVideoMediaMetadata().getDurationMillis() : null)
                .build();
    }

    private Permission getPermission() {
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        return permission;
    }

    public List<FileRelationshipDTO> toDTOS(List<FileRelationship> entities) {
        if (entities.isNullOrEmpty()) return new ArrayList<>();
        List<FileRelationshipDTO> dtos = new ArrayList<>();
        for (FileRelationship entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    public FileRelationshipDTO toDTO(FileRelationship entity) {
        if (entity == null) return new FileRelationshipDTO();
        return FileRelationshipDTO.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .parentType(entity.getParentType())
                .fileId(entity.getFileId())
                .mimeType(entity.getMimeType())
                .name(entity.getName())
                .size(entity.getSize())
                .duration(entity.getDuration())
                .webViewLink(entity.getWebViewLink())
                .downloadLink(entity.getDownloadLink())
                .pathFile(entity.getPathFile())
                .build();
    }
}
