package com.elearning.apis;

import com.elearning.controller.FileController;
import com.elearning.models.dtos.FileRelationshipDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file-relationship")
@RequiredArgsConstructor
@Tag(name = "FileRelationship", description = "FileRelationship API")
public class FileRelationshipAPI {
    @Autowired
    FileController fileController;

    @Operation(summary = "Upload file")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileRelationshipDTO uploadFile(@RequestPart(value = "file") MultipartFile file,
                                          @RequestParam(value = "parent_id") String parentId,
                                          @RequestParam(value = "file_type") String fileType,
                                          @RequestParam(value = "create_by") String createBy) {
        return fileController.saveFile(file, parentId, fileType, createBy);
    }

    @Operation(summary = "Xoá File")
    @DeleteMapping("/delete/{id}")
    public void deleteFile(@PathVariable(value = "id") String id) throws Exception {
        fileController.deleteFile(id);
    }
}
