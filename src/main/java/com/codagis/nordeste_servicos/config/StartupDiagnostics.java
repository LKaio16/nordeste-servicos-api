package com.codagis.nordeste_servicos.config;

import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupDiagnostics implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupDiagnostics.class);

    private final Environment env;
    private final ApplicationContext ctx;

    public StartupDiagnostics(Environment env, ApplicationContext ctx) {
        this.env = env;
        this.ctx = ctx;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] profiles = env.getActiveProfiles();
        log.info("Startup: spring.profiles.active={}", profiles.length == 0 ? "<default>" : Arrays.toString(profiles));
        log.info("Startup: gcloud.enabled={}", env.getProperty("gcloud.enabled", "true"));
        log.info("Startup: gcloud.bucket={}", env.getProperty("gcloud.bucket"));

        String b64 = env.getProperty("gcloud.credentials-b64");
        String json = env.getProperty("gcloud.credentials-json");
        String path = env.getProperty("gcloud.credentials-path");

        log.info("Startup: gcloud.credentials-b64.present={} len={}", b64 != null && !b64.isBlank(),
                b64 == null ? 0 : b64.trim().length());
        log.info("Startup: gcloud.credentials-json.present={} len={}", json != null && !json.isBlank(),
                json == null ? 0 : json.trim().length());
        log.info("Startup: gcloud.credentials-path.present={}", path != null && !path.isBlank());

        int storageBeans = ctx.getBeansOfType(Storage.class).size();
        log.info("Startup: Storage bean count={}", storageBeans);
    }
}
