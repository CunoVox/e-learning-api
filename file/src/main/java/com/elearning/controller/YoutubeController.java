package com.elearning.controller;

import com.elearning.models.dtos.FileDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeController {

    private static YouTube youtube;

    @Autowired
    private AuthController authController;

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
    public FileDTO uploadVideo(MultipartFile file) {
        String accessToken = authController.getAccessToken();
        String VIDEO_FILE_FORMAT = "video/*";
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "youtube-cmdline-uploadvideo").build();
            Video videoObjectDefiningMetadata = new Video();

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("unlisted");
            status.setEmbeddable(true);
            videoObjectDefiningMetadata.setStatus(status);

            VideoSnippet snippet = new VideoSnippet();

            File videoFile = multipartToFile(file);
            snippet.setTitle(file.getOriginalFilename());
            videoObjectDefiningMetadata.setSnippet(snippet);
            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
                    new FileInputStream(videoFile));

            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status,contentDetails", videoObjectDefiningMetadata, mediaContent);
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);

            // Call the API and upload the video.
            Video returnedVideo = videoInsert.execute();
            log.info("----uploaded {}----", returnedVideo.getSnippet().getTitle());
            return toFileDTO(returnedVideo);
        } catch (GoogleJsonResponseException e) {
            log.error("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            log.error("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
        return null;
    }

    private FileDTO toFileDTO(Video file) {
        return FileDTO.builder()
                .id(file.getId())
                .name(file.getSnippet().getTitle())
                .videoMediaMetadata(file.getContentDetails().getDuration())
                .webViewLink("https://www.youtube.com/watch?v=" + file.getId())
                .build();
    }

    public  static File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }
}
