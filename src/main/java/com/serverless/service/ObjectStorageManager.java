package com.serverless.service;

import com.serverless.model.Content;

import java.io.IOException;

public interface ObjectStorageManager {
    public void saveObject(final String bucketName, final String objectKey, final Content content);
    public Content getObject(final String bucketName, final String objectKey) throws IOException;
    public boolean deleteObject(final String bucketName, final String objectKey);
}
