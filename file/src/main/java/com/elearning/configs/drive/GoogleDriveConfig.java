package com.elearning.configs.drive;

import com.elearning.utils.Constants;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Configuration
@Slf4j
public class GoogleDriveConfig {
    @Autowired
    private GoogleCredential googleCredential;

    @Bean
    public Drive getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT,
                JacksonFactory.getDefaultInstance(), googleCredential)
                .build();
    }

    @Bean
    public GoogleCredential googleCredential() throws GeneralSecurityException, IOException, NoSuchFieldException {
        Collection<String> elenco = new ArrayList<>();
        elenco.add("https://www.googleapis.com/auth/drive");
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(Constants.SERVICE_ACCOUNT_ID)
                    .setServiceAccountScopes(elenco)
                    .setServiceAccountPrivateKeyFromP12File(new File((new File(GoogleDriveConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())) + File.separator + "credentials.p12"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return new GoogleCredential();
        }
    }
}
