package com.elearning.apis;

import com.elearning.controller.FileRelationshipController;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file-relationship")
@RequiredArgsConstructor
@Tag(name = "FileRelationship", description = "FileRelationship API")
public class FileRelationshipAPI {
    @Autowired
    FileRelationshipController fileRelationshipController;

    @Operation(summary = "Upload file")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileRelationshipDTO uploadFile(@RequestPart(value = "file") MultipartFile file,
                                          @RequestParam(value = "parent_id") String parentId,
                                          @RequestParam(value = "parent_type") EnumParentFileType parentType) {
        return fileRelationshipController.saveFile(file, parentId, parentType.name());
    }
    @Operation(summary = "Xoá file bằng id")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteFile(@PathVariable(value = "id") String id) throws Exception {
        fileRelationshipController.deleteFile(id);
    }

    @Operation(summary = "Xoá file bằng path file")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_LECTURE', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public void deleteFileByPathFile(@RequestParam(value = "path_file") String pathFile) {
        fileRelationshipController.deleteFileByPathFile(pathFile);
    }
}
