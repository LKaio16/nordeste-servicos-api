package com.codagis.nordeste_servicos.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnProperty(name = "gcloud.enabled", havingValue = "true", matchIfMissing = true)
public class GcsConfig {

    @Value("${gcloud.bucket:ne-servicos}")
    private String bucketName;

    @Value("${gcloud.credentials-path:}")
    private String credentialsPath;

    @Value("${gcloud.credentials-json:}")
    private String credentialsJson;

    @Bean
    public Storage storage() throws IOException {
        StorageOptions.Builder builder = StorageOptions.newBuilder();

        if (credentialsJson != null && !credentialsJson.isBlank()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8)));
            builder.setCredentials(credentials);
        } else if (credentialsPath != null && !credentialsPath.isBlank()) {
            builder.setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)));
        } else {
            builder.setCredentials(GoogleCredentials.getApplicationDefault());
        }

        return builder.build().getService();
    }

    @Bean
    public String gcsBucketName() {
        return bucketName;
    }
}
