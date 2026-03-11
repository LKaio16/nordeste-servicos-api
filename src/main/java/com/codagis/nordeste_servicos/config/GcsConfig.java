package com.codagis.nordeste_servicos.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@Conditional(GcsEnabledCondition.class)
public class GcsConfig {

    private static final Logger log = LoggerFactory.getLogger(GcsConfig.class);

    @Value("${gcloud.bucket:ne-servicos}")
    private String bucketName;

    @Value("${gcloud.credentials-path:}")
    private String credentialsPath;

    @Value("${gcloud.credentials-json:}")
    private String credentialsJson;

    @Bean
    public Storage storage() throws IOException {
        log.info("Configurando Google Cloud Storage (bucket: {})", bucketName);
        StorageOptions.Builder builder = StorageOptions.newBuilder();

        // Usa JSON inline só se parecer um JSON válido (evita parse de caminho ou valor inválido)
        String json = (credentialsJson != null && !credentialsJson.isBlank()) ? credentialsJson.trim() : null;
        if (json != null && json.startsWith("{")) {
            try {
                // Em variável de ambiente, \n na chave privada pode vir como literal backslash-n; normaliza para newline
                String normalized = json.replace("\\n", "\n");
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8)));
                builder.setCredentials(credentials);
                log.info("GCS: credenciais carregadas a partir de GCLOUD_CREDENTIALS_JSON");
            } catch (Exception e) {
                log.error("GCLOUD_CREDENTIALS_JSON inválido: {}", e.getMessage());
                throw new IllegalStateException(
                        "GCLOUD_CREDENTIALS_JSON deve ser um JSON válido de conta de serviço (começando com {\"type\":\"service_account\"...). Erro: " + e.getMessage(), e);
            }
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
