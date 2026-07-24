package com.example.storage;

import org.springframework.stereotype.Service;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements MinioStorageService {
    private final MinioClient minioClient; // dùng để tương tác với MinIO server
    private final MinioProperties minioProperties; // chứa các thông tin cấu hình của MinIO
    
}
