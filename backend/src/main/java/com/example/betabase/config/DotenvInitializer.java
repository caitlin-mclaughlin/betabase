package com.example.betabase.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();
                
        Map<String, Object> envProps = new HashMap<>();
        for (DotenvEntry entry : dotenv.entries()) {
            envProps.put(entry.getKey(), entry.getValue());
        }

        ConfigurableEnvironment env = applicationContext.getEnvironment();
        env.getPropertySources().addFirst(new MapPropertySource("dotenv", envProps));
    }
}
