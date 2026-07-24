package com.example.config.minIO;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.storage.MinioStorageService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Component
public class MinioBucketInitializer implements ApplicationRunner {
    private final MinioStorageService minioStorageService;
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
        minioStorageService.ensureBucketExists();
    }
}
