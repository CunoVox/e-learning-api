package com.elearning.controller;

import com.elearning.entities.FileRelationship;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.reprositories.IFileRelationshipRepository;
import com.elearning.utils.Constants;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.experimental.ExtensionMethod;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
@ExtensionMethod(Extensions.class)
public class FileRelationshipController extends BaseController {
    @Autowired
    Drive googleDrive;

    @Autowired
    IFileRelationshipRepository fileRelationshipRepository;

    private File sendFileToGoogleDrive(MultipartFile fileToUpload) {
        try {
            if (null != fileToUpload) {
                File fileMetadata = new File();
                fileMetadata.setParents(Collections.singletonList(Constants.FOLDER_TO_UPLOAD));
                fileMetadata.setName(ObjectId.get().toString());
                File uploadFile = googleDrive
                        .files()
                        .create(fileMetadata,
                                new InputStreamContent(fileToUpload.getContentType(),
                                        new ByteArrayInputStream(fileToUpload.getBytes()))
                        )
                        .setFields("id, size, mimeType, webViewLink, videoMediaMetadata, webContentLink").execute();
                googleDrive.permissions().create(uploadFile.getId(), getPermission()).execute();
                return uploadFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPathFile(FileRelationshipDTO fileRelationshipDTO){
        if (fileRelationshipDTO !=null){
            switch (EnumParentFileType.valueOf(fileRelationshipDTO.getParentType())) {
                // Case 1
                case COURSE_VIDEO:
                    return Constants.BASE_VIDEO_URL + fileRelationshipDTO.getFileId() + "/preview";
                // Case 2
                case COURSE_IMAGE:
                    // Case 3
                case CATEGORY_IMAGE:
                    return Constants.BASE_IMAGE_URL + fileRelationshipDTO.getFileId();

                default:
                    return null;
            }
        }
        return null;
    }

    public List<FileRelationshipDTO> getFileRelationships(List<String> parentIds, String type){
        List<FileRelationship> fileRelationships = fileRelationshipRepository.findAllByParentIdInAndParentType(parentIds, type);
        return toDTOS(fileRelationships);
    }

    public Map<String, String> getUrlOfFile(List<FileRelationshipDTO> fileRelationshipDTOS){
        Map<String, String> map = new HashMap<>();
        for (FileRelationshipDTO fileRelationshipDTO : fileRelationshipDTOS){
            String url = getPathFile(fileRelationshipDTO);
            if (!url.isBlankOrNull()){
                map.put(fileRelationshipDTO.getParentId(), url);
            }
        }
        return map;
    }

    public void deleteFileToGoogleDrive(String fileId) throws Exception {
        googleDrive.files().delete(fileId).execute();
    }

    public void deleteFile(String id) throws Exception {
        Optional<FileRelationship> fileRelationship = fileRelationshipRepository.findById(id);
        if (fileRelationship.isEmpty()) {
            throw new ServiceException("Không tìm thấy file trong hệ thống");
        }
        deleteFileToGoogleDrive(fileRelationship.get().getFileId());
        fileRelationshipRepository.deleteById(id);
    }

    public FileRelationshipDTO saveFile(MultipartFile multipartFile, String parentId, String parentType) {
        String userId = this.getUserIdFromContext();
        File fileDrive = sendFileToGoogleDrive(multipartFile);
        if (fileDrive == null) {
            throw new ServiceException("Tải file lên không thành công");
        }
        FileRelationship fileRelationship = buildFileDriveToFileRelationship(fileDrive);
        fileRelationship.setParentId(parentId);
        fileRelationship.setParentType(parentType);
        fileRelationship.setName(multipartFile.getOriginalFilename());
        fileRelationship.setCreatedAt(new Date());
        fileRelationship.setCreatedBy(userId);
        FileRelationship fileRelationshipSaved = fileRelationshipRepository.save(fileRelationship);
        return toDTO(fileRelationshipSaved);
    }

    public FileRelationship buildFileDriveToFileRelationship(File fileDrive) {
        if (fileDrive == null) return new FileRelationship();
        return FileRelationship.builder()
                .fileId(fileDrive.getId() != null ? fileDrive.getId() : null)
                .name(fileDrive.getName() != null ? fileDrive.getName() : null)
                .size(fileDrive.getSize() != null ? fileDrive.getSize() : null)
                .mimeType(fileDrive.getMimeType() != null ? fileDrive.getMimeType() : null)
                .webViewLink(fileDrive.getWebViewLink() != null ? fileDrive.getWebViewLink() : null)
                .duration(fileDrive.getVideoMediaMetadata() != null
                        && fileDrive.getVideoMediaMetadata().getDurationMillis() != null ? fileDrive.getVideoMediaMetadata().getDurationMillis() : null)
                .build();
    }

    private Permission getPermission() {
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        return permission;
    }

    public List<FileRelationshipDTO> toDTOS(List<FileRelationship> entities){
        if (entities.isNullOrEmpty()) return new ArrayList<>();
        List<FileRelationshipDTO> dtos = new ArrayList<>();
        for (FileRelationship entity : entities){
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
                .build();
    }
}
