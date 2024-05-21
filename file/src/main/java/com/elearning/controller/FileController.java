package com.elearning.controller;

import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.FileDTO;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Constants;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
@ExtensionMethod(Extensions.class)
public class FileController extends BaseController {
    @Autowired
    private Drive googleDrive;

    @Autowired
    private YoutubeController youtubeController;

    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;

    public FileDTO uploadFile(MultipartFile file, String type) throws IOException {
        //nếu là video thì upload lên youtube
//        if (Objects.requireNonNull(file.getContentType()).contains("video") && !type.equals(EnumParentFileType.COURSE_ATTACHMENT.name())) {
//            return youtubeController.uploadVideo(file);
//        }
        return sendFileToGoogleDrive(file);
    }

    public FileDTO sendFileToGoogleDrive(MultipartFile fileToUpload) throws IOException {
        if (null == fileToUpload) {
            throw new ServiceException("File is null");
        }
        File fileMetadata = new File();
        String name = sequenceValueItemRepository.getSequence(FileDTO.class) + "-" + fileToUpload.getOriginalFilename();
        fileMetadata.setParents(Collections.singletonList(Constants.FOLDER_TO_UPLOAD));
        fileMetadata.setName(name);
        File uploadFile = googleDrive
                .files()
                .create(fileMetadata,
                        new InputStreamContent(fileToUpload.getContentType(),
                                new ByteArrayInputStream(fileToUpload.getBytes()))
                )
                .setFields("id, size, mimeType, webViewLink, videoMediaMetadata, webContentLink").execute();
        googleDrive.permissions().create(uploadFile.getId(), getPermission()).execute();
        return toFileDTO(uploadFile, name);
    }

    public FileDTO toFileDTO(File file, String name) {
        return FileDTO.builder()
                .id(file.getId())
                .name(name)
                .mimeType(file.getMimeType())
                .webViewLink(file.getWebViewLink())
                .webContentLink(file.getWebContentLink())
                .videoMediaMetadata(String.valueOf(file.getVideoMediaMetadata()))
                .size(String.valueOf(file.getSize()))
                .build();
    }

    private Permission getPermission() {
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        return permission;
    }
}
