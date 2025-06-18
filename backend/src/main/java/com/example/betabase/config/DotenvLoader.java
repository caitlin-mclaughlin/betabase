package com.example.betabase.config;

import io.github.cdimascio.dotenv.Dotenv;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/*@Component
public class DotenvLoader {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir")) // defaults to root dir
                .ignoreIfMissing() // optional, no error if missing in prod
                .load();

        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
*/