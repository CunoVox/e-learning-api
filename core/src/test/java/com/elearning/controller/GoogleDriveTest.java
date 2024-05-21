//package com.elearning.controller;
//
//import com.elearning.utils.Constants;
//import com.google.api.client.http.FileContent;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.Permission;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//
//@SpringBootTest
//public class GoogleDriveTest {
//    @Autowired
//    Drive googleDrive;
//
//
//    @Test
//    public void createNewFolder() throws IOException {
//        File fileMetadata = new File();
//        fileMetadata.setName("HaoHao");
//        fileMetadata.setMimeType("application/vnd.google-apps.folder");
//        File file = googleDrive.files().create(fileMetadata).setFields("id").execute();
//        System.out.println(file.getId());
//    }
//
//    public Permission setPermission(String type, String role){
//        Permission permission = new Permission();
//        permission.setType(type);
//        permission.setRole(role);
//        return permission;
//    }
//
//    @Test
//    public void uploadFile() throws IOException, GeneralSecurityException {
//        File newGGDriveFile = new File();
//        java.io.File fileToUpload = new java.io.File("C:/Users/DELL/Downloads/Đen - Nấu ăn cho em ft. PiaLinh (M-V).mp4");
//        newGGDriveFile.setParents(Collections.singletonList("10kTLDsugv7nvEUERus8oV12CHwSm8vWq")).setName(ObjectId.get().toString());
//        FileContent mediaContent = new FileContent("video/mp4", fileToUpload);
//        File file = googleDrive.files().create(newGGDriveFile, mediaContent).setFields("id,webViewLink,thumbnailLink,webContentLink").execute();
//        googleDrive.permissions().create(file.getId(), setPermission("anyone", "reader")).execute();
//        System.out.println(file.getId());
//    }
//
//    @Test
//    public void getAllFile() throws IOException {
//        String query = "'" + Constants.FOLDER_TO_UPLOAD + "' in parents";
////        FileList result = googleDrive.files().list()
////                .setQ(query)
////                .setFields("nextPageToken, files(id, name, size, thumbnailLink, shared)") // get field of google drive folder
////                .execute();
//        File result = googleDrive.files().get("1EjTLKjisTQH8ZzF99IDqJRe_KHKJfAVR")
//                .setFields("id, name,mimeType, size, webViewLink, thumbnailLink, shared, videoMediaMetadata") // get field of google drive folder
//                .execute();
//        Assertions.assertNotNull(result);
//    }
//}
