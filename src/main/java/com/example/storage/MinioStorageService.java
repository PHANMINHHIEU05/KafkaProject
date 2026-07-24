package com.example.storage;

public interface MinioStorageService {
    void ensureBucketExists();
    String createUpLoadUrl(String objectKey);
    String createDownloadUrl(String objectKey);
    StoredObjectMetadata startObject(String object);
    void deleteObject(String objectKey);
}
