package com.codagis.nordeste_servicos.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Habilita GCS quando: gcloud.enabled for true (qualquer variação) OU GCLOUD_CREDENTIALS_JSON estiver preenchido.
 */
public class GcsEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabled = context.getEnvironment().getProperty("gcloud.enabled", "false");
        if (enabled != null) {
            enabled = enabled.trim();
            if (enabled.startsWith("\"") && enabled.endsWith("\"")) {
                enabled = enabled.substring(1, enabled.length() - 1).trim();
            }
            if ("true".equalsIgnoreCase(enabled)) {
                return true;
            }
        }
        String json = context.getEnvironment().getProperty("gcloud.credentials-json", "");
        return json != null && !json.isBlank() && json.trim().startsWith("{");
    }
}
