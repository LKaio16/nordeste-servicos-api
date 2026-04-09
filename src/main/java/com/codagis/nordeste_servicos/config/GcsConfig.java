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

    // ATENÇÃO: somente para teste temporário. Não commite credenciais reais em repositório.
    private static final String HARDCODED_CREDENTIALS_B64 = "ew0KICAidHlwZSI6ICJzZXJ2aWNlX2FjY291bnQiLA0KICAicHJvamVjdF9pZCI6ICJub3JkZXN0ZS1zZXJ2aWNvcyIsDQogICJwcml2YXRlX2tleV9pZCI6ICIwODhmZTZjNTA0ODJjMmJmMjgxZjg1MTYwMTE3NzkxNTg3MGM3MWJjIiwNCiAgInByaXZhdGVfa2V5IjogIi0tLS0tQkVHSU4gUFJJVkFURSBLRVktLS0tLVxuTUlJRXZRSUJBREFOQmdrcWhraUc5dzBCQVFFRkFBU0NCS2N3Z2dTakFnRUFBb0lCQVFETEx0cmhDYm1iLzVIQ1xuRTkrQW1tbVp0NVJzaTYyNDRyakYyN0Q1Rnk2enVEYVkwY2xTRm9oYk5sRmdMY0hDSy9kakFCVnFBVU1renVxVlxuZ3dRcFJRTDBobG1iVmo5TGhJditBSlNTQzdCczExelU0djdBcHV5Z2ZXQzYxMndSM2JiR3Q1UzBnNnVNTVRQOVxuSkd4VVhyVW1KOUZZR0daZmx2QTZYc0pFQ2dWR0RCUUpaRlJmOGFIVm0ra1RMMnFSeXJnMzVYK24vUElVU1BjOVxuMTdOcUZERTlCOW04QUV1U3hHaTFwcSs0aDY0djF6N0h6dkszdG95S3RlWkRQRDRJakpXTUx0aUpMVUtaNzNDZVxubVpMWXloaEtiSW8vcXBCbXVVbHVXcml2ZVhIeDdOVkVNMFhKTHY0bm1obUYrakg4UEVIM1pjdkNwUGZkclVsRlxuNkVyWXUyZVBBZ01CQUFFQ2dnRUFTeDNlYzlZcGxiL05mbWZrZUdJZDZtT1JidlBRM1FxTTd6RkNZMFNyMG11blxuVzZZcmZMcXgzSzdmTk9TVnZwaU1sRUJDcW5jZkFLdXkxR1dra3pGZFgxT1FLenFlbWVlOTdjRC9MR00vN1cxTlxuZDdNWUpkZFFvaURZTXh0VGErT0JJRVJjcU1RSkZ3SlZ5eCs5SVJBR2ZyMjN5bzd5d2lKR1cvNE9haU1lUWYrcVxuTmh1Y1V2S2QxQ0xOQWxOa2VUdFhHVGhHWHNVOXVGQkRSZ3ZtTHVLM29Ib3hMWHVQNWlGV2hXbTRXMnFVWXRJcVxuOEpkQTV3SW9HcVhKMll2QUo3SHN4c1RTeHZveDdFZ0c1cXg1OUdmM2FFaXZpM1ZFMThoRnNpUExjaC8yZi9BNVxuRWtEY01JWDdPQVhlRUVFb2laanVRUXE4YjB2c08xdGEzQnpJWDMyZlVRS0JnUUR4L3hkeDFHN3YvY2w4UzhneFxuV1NXNm9xYWZuSFdjNHRHTkpwRjd3N1g0UEVlQXhjRHZ4V1FTMUtnNTBwRVV4NzNSUll2NDY0SE42TkUrbGV2clxuOEJDWG4wTm9OY1RZRjJ3MEJpaFZpbnZ4bTk0TkFWNC9mMW9lOGhaV25oc3Bpa3ZRaG1wclNuemZMeDdTZEx5Q1xub0hXUVlFK3ZwYnF6ZHladDJXMVAwKzlBSndLQmdRRFc4TWtpZ1RXZWdwb0xRKzJYY09DL1hZY21ZYVQ1c3BvN1xudHRFYVluNThRS1VjeFkxZmNLMzZIZm8vU0M4Zzdqb1prNFl3eE45cDVYYUM1TXljSmFDZVpHeDJrb1NiVnhPelxubjNtbllMRFJKZzR0NmRZdng0RUEyZ05DY2ZKSVhST2RtbUVQZW9VOWJwTTljMzJPeVZ3MmIxWjJITTVBWktFWlxudEZwajdKZFdXUUtCZ0hCTjM4VjF2cDRsZXZIeUFVL1ZmVXJsM01uSnhGTXdkK3MzY09DczdLL3MyWW9MUEFTSlxuNkcxTXYxMUdPUW9ad0I5MFZRY0oxRTJJdEVLRk5OMWpwVytYKzZKTEFiVCsrYm9TK1hqcE1hZm9tOTRST25uaFxuVWlOMldhT004RElRK2lvbGx4Ri9tZi9CVUY1K3lsVWN2akpvWDlLMTZMVU1vSXJ1RW1xdmdpS1ZBb0dBSjJ6UlxuSitBVmFyYS85TE1JQUpBMjJVTVppQ08rOUFJYm9qcGRUbjZrZnJ3SXpTYzdVRGRGNGl2ZmUvNE1qdmpEZFcrR1xuM0trdjVhZXVQa0lqZWJ5NUluUi9KSnlwY0NqZnQwSFlPYWhWcG5kWmdIaDB6aHpFLzlmOVl4R3RoREoyWjZFcVxuam9GRnNVZjMvL3lRR2tHN0JVMFV5ZU5zdllqM3Q5UktCQ3dnY2prQ2dZRUE3ekZIVzBRRUFYVE5EaUtzTTJuOFxuVFcveis5U0xCTlI4YTV3bUxNeFF5SnB3MDB4VjZua3BXRnkyRmhoTjE5SFY2MVFMVWM0VzIrK0F0WWswMlNhaVxuUVNIL2N0Y2JYa1JDVWh0ekt6dlkyYVgvQVR2c1R5VDZIdlZkbWszN2JhK3Qyd2p1c0VqSUdUY2t6YVVJTnl0RFxueEZCMVFpdTlwR2tGdEVqNHB3NmdSSWc9XG4tLS0tLUVORCBQUklWQVRFIEtFWS0tLS0tXG4iLA0KICAiY2xpZW50X2VtYWlsIjogImNvbnRhLXNlcnZpY28tbmUtc2Vydmljb3NAbm9yZGVzdGUtc2Vydmljb3MuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLA0KICAiY2xpZW50X2lkIjogIjExNzM3NDQyODIyNTQzODkzMjgyNiIsDQogICJhdXRoX3VyaSI6ICJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsDQogICJ0b2tlbl91cmkiOiAiaHR0cHM6Ly9vYXV0aDIuZ29vZ2xlYXBpcy5jb20vdG9rZW4iLA0KICAiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjogImh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL29hdXRoMi92MS9jZXJ0cyIsDQogICJjbGllbnRfeDUwOV9jZXJ0X3VybCI6ICJodHRwczovL3d3dy5nb29nbGVhcGlzLmNvbS9yb2JvdC92MS9tZXRhZGF0YS94NTA5L2NvbnRhLXNlcnZpY28tbmUtc2Vydmljb3MlNDBub3JkZXN0ZS1zZXJ2aWNvcy5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsDQogICJ1bml2ZXJzZV9kb21haW4iOiAiZ29vZ2xlYXBpcy5jb20iDQp9";

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
        if (b64 == null && HARDCODED_CREDENTIALS_B64 != null && !HARDCODED_CREDENTIALS_B64.isBlank()) {
            b64 = HARDCODED_CREDENTIALS_B64.trim();
            log.warn("GCS: usando credenciais hardcoded (somente teste)");
        }
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
