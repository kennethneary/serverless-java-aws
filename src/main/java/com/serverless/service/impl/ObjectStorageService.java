package com.serverless.service.impl;

import com.serverless.model.Content;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.serverless.config.DependencyInjector.injector;

public class ObjectStorageService implements ObjectStorageManager {

    private S3Client s3 = injector.getInstance(S3Client.class);

    public void saveObject(final String bucketName, final String objectKey, final Content content) {
        final String base64Content = content.getBase64Content();
        final String contentType = content.getContentType();
        final PutObjectRequest putBuilder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        final RequestBody requestBody = RequestBody.fromString(base64Content);
        this.s3.putObject(putBuilder, requestBody);
    }

    public Content getObject(final String bucketName, final String objectKey) throws IOException {
        final GetObjectRequest putBuilder = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        final ResponseInputStream<GetObjectResponse> objectResponse = this.s3.getObject(putBuilder);
        final String content = IOUtils.toString(objectResponse, StandardCharsets.UTF_8);
        return Content.builder()
                .base64Content(content)
                .contentType(objectResponse.response().contentType())
                .build();
    }

    public boolean deleteObject(final String bucketName, final String objectKey) {
        final DeleteObjectRequest deleteBuilder = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        this.s3.deleteObject(deleteBuilder);
        return true;
    }
}