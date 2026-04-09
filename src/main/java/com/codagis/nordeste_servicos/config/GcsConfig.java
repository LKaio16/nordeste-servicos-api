package com.codagis.nordeste_servicos.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@Profile("!test")  // sempre ativo exceto em testes (evita precisar de credenciais reais)
public class GcsConfig {

    private static final Logger log = LoggerFactory.getLogger(GcsConfig.class);

    @Value("${gcloud.bucket:ne-servicos}")
    private String bucketName;

    @Value("${gcloud.credentials-path:}")
    private String credentialsPath;

    @Value("${gcloud.credentials-json:}")
    private String credentialsJson;

    @Value("${gcloud.credentials-b64:}")
    private String credentialsB64;

    @Bean
    public Storage storage() throws IOException {
        log.info("Configurando Google Cloud Storage (bucket: {})", bucketName);
        StorageOptions.Builder builder = StorageOptions.newBuilder();

        String b64 = (credentialsB64 != null && !credentialsB64.isBlank()) ? credentialsB64.trim() : null;
        if (b64 != null) {
            try {
                byte[] decoded = Base64.getMimeDecoder().decode(b64);
                GoogleCredentials credentials = credentialsFromServiceAccountJson(
                        new String(decoded, StandardCharsets.UTF_8));
                builder.setCredentials(credentials);
                log.info("GCS: credenciais carregadas a partir de GCLOUD_CREDENTIALS_B64");
            } catch (IllegalArgumentException e) {
                log.error("GCLOUD_CREDENTIALS_B64 não é Base64 válido: {}", e.getMessage());
                throw new IllegalStateException(
                        "GCLOUD_CREDENTIALS_B64 deve ser o JSON da conta de serviço codificado em Base64. Erro de decodificação: "
                                + e.getMessage(), e);
            } catch (Exception e) {
                log.error("GCLOUD_CREDENTIALS_B64 decodificado mas JSON inválido: {}", e.getMessage());
                throw new IllegalStateException(
                        "GCLOUD_CREDENTIALS_B64 (após decodificar) deve ser JSON válido de conta de serviço. Erro: "
                                + e.getMessage(), e);
            }
        } else {
            // JSON inline só se começar com {; senão arquivo ou ADC
            String json = (credentialsJson != null && !credentialsJson.isBlank()) ? credentialsJson.trim() : null;
            if (json != null && json.startsWith("{")) {
                try {
                    GoogleCredentials credentials = credentialsFromServiceAccountJson(json);
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
        }

        return builder.build().getService();
    }

    private static GoogleCredentials credentialsFromServiceAccountJson(String json) throws IOException {
        String normalized = json.replace("\\n", "\n");
        return GoogleCredentials.fromStream(
                new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8)));
    }

    @Bean
    public String gcsBucketName() {
        return bucketName;
    }
}
