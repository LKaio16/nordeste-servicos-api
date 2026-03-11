package com.codagis.nordeste_servicos.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Considera GCS habilitado quando gcloud.enabled for true (aceita "true", true, "TRUE", "\"true\"" no Railway).
 */
public class GcsEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String value = context.getEnvironment().getProperty("gcloud.enabled", "false");
        if (value == null) return false;
        value = value.trim();
        return "true".equalsIgnoreCase(value) || "\"true\"".equals(value);
    }
}
