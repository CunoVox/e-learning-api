package com.elearning.apis;

import com.elearning.controller.FileController;
import com.elearning.controller.YoutubeController;
import com.elearning.models.dtos.FileDTO;
import com.google.api.services.drive.model.File;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
@Tag(name = "File", description = "File API")
public class FileAPI {
    @Autowired
    private YoutubeController youtubeController;
    @Autowired
    private FileController fileController;
    @Operation(summary = "Upload video")
    @PostMapping(path = "/upload/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadVideo(@RequestPart(value = "file") MultipartFile file) {
        return youtubeController.uploadVideo(file);
    }

    @Operation(summary = "Upload file")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadFile(@RequestPart(value = "file") MultipartFile file,
                              @RequestParam(value = "type") String type) throws IOException {
        return fileController.uploadFile(file,type);
    }

}
