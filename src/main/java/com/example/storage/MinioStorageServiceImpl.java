package com.example.storage;

import org.springframework.stereotype.Service;

import com.example.config.minIO.MinioProperties;
import com.example.exception.ErrorCode;
import com.example.exception.StorageException;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.Http;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements MinioStorageService {
    private final MinioClient minioClient; // dùng để tương tác với MinIO server
    private final MinioProperties minioProperties; // chứa các thông tin cấu hình của MinIO
    @Override
    public void ensureBucketExists(){
        try{
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                .bucket(minioProperties.bucket()).build()
            );
            if (!exists){
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                    .bucket(minioProperties.bucket()).build()
                );
            }
        } catch(MinioException e){
            throw new StorageException("Error while ensuring bucket exists: " + e.getMessage());
        }
    }
    @Override
    public String createUploadUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.PUT)
                            .bucket(minioProperties.bucket())
                            .object(objectKey)
                            .expiry(
                                    minioProperties
                                            .uploadUrlExpirySeconds()
                            )
                            .build()
            );
        } catch (MinioException exception) {
            throw new StorageException(
                    "Không thể tạo upload URL",
                    exception
            );
        }
    }
    @Override 
    public String createDownloadUrl(String objectKey){
        try{
            String url = minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder()
                .method(Http.Method.GET)
                .bucket(minioProperties.bucket())
                .object(objectKey)
                .expiry(minioProperties.uploadUrlExpirySeconds()) 
                .build()
            );
            return url;
        } catch(MinioException e){
            throw new StorageException("Error while creating download URL: " + e.getMessage());
        }
    }
    @Override
    public void deleteObject(String objectKey){
        try{
            minioClient.removeObject(
                io.minio.RemoveObjectArgs.builder()
                .bucket(minioProperties.bucket())
                .object(objectKey)
                .build()
            );
        } catch(MinioException e){
            throw new StorageException("Error while deleting object: " + e.getMessage());
        }
    }   
}
