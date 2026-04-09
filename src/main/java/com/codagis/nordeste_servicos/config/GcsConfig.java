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

    /** Base64 do JSON completo (recomendado em plataformas como Railway). */
    @Value("${gcloud.credentials-json-b64:}")
    private String credentialsJsonB64;

    @Value("${gcloud.credentials-json:}")
    private String credentialsJson;

    @Bean
    public Storage storage() throws IOException {
        log.info("Configurando Google Cloud Storage (bucket: {})", bucketName);
        StorageOptions.Builder builder = StorageOptions.newBuilder();

        String json = resolveServiceAccountJson();
        if (json != null && json.startsWith("{")) {
            try {
                String normalized = json.replace("\\n", "\n");
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8)));
                builder.setCredentials(credentials);
            } catch (Exception e) {
                log.error("Credenciais GCS inválidas após decodificar/parse: {}", e.getMessage());
                throw new IllegalStateException(
                        "Credenciais GCS inválidas: o JSON da conta de serviço não pôde ser lido. "
                                + "Se usar Base64, confira GCLOUD_CREDENTIALS_JSON_B64 (JSON completo, sem cortes). "
                                + "Se usar JSON inline, confira GCLOUD_CREDENTIALS_JSON. Erro: " + e.getMessage(), e);
            }
        } else if (credentialsPath != null && !credentialsPath.isBlank()) {
            builder.setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)));
            log.info("GCS: credenciais carregadas a partir de GOOGLE_APPLICATION_CREDENTIALS (arquivo)");
        } else {
            builder.setCredentials(GoogleCredentials.getApplicationDefault());
            log.info("GCS: usando Application Default Credentials");
        }

        return builder.build().getService();
    }

    /**
     * Prioridade: {@code GCLOUD_CREDENTIALS_JSON_B64} → {@code GCLOUD_CREDENTIALS_JSON} → nulo (ADC/caminho tratados em {@link #storage()}).
     */
    private String resolveServiceAccountJson() {
        if (credentialsJsonB64 != null && !credentialsJsonB64.isBlank()) {
            try {
                String compact = credentialsJsonB64.trim().replaceAll("\\s+", "");
                byte[] decoded = Base64.getDecoder().decode(compact);
                String json = new String(decoded, StandardCharsets.UTF_8).trim();
                log.info("GCS: credenciais carregadas a partir de GCLOUD_CREDENTIALS_JSON_B64 ({} bytes decodificados)", decoded.length);
                if (!json.startsWith("{")) {
                    throw new IllegalStateException("Após decodificar Base64, o conteúdo deve começar com '{' (JSON da conta de serviço).");
                }
                return json;
            } catch (IllegalArgumentException e) {
                log.error("GCLOUD_CREDENTIALS_JSON_B64 inválido: {}", e.getMessage());
                throw new IllegalStateException(
                        "GCLOUD_CREDENTIALS_JSON_B64 não é um Base64 válido. No PowerShell: "
                                + "[Convert]::ToBase64String([IO.File]::ReadAllBytes('service-account.json')) "
                                + "e cole o resultado inteiro na variável. Erro: " + e.getMessage(), e);
            }
        }

        String json = (credentialsJson != null && !credentialsJson.isBlank()) ? credentialsJson.trim() : null;
        if (json != null && json.startsWith("{")) {
            log.info("GCS: credenciais carregadas a partir de GCLOUD_CREDENTIALS_JSON");
            return json;
        }
        if (json != null && !json.isEmpty()) {
            log.warn("GCS: GCLOUD_CREDENTIALS_JSON está definido mas não começa com '{'; ignorando (use B64 ou JSON completo).");
        }
        return null;
    }

    @Bean
    public String gcsBucketName() {
        return bucketName;
    }
}
