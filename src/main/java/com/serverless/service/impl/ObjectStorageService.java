package com.serverless.service.impl;

import com.google.inject.Inject;
import com.serverless.model.Content;
import com.serverless.service.ObjectStorageManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class ObjectStorageService implements ObjectStorageManager {

    private static final Logger LOG = LogManager.getLogger(ObjectStorageService.class);

    @Inject
    private S3Client s3;

    @Override
    public void saveObject(final String bucketName, final String objectKey, final Content content) {
        LOG.info("saveObject - bucketName: {}, objectKey: {}, content: {}", bucketName, objectKey, content);

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

    @Override
    public Content getObject(final String bucketName, final String objectKey) throws IOException {
        LOG.info("getObject - bucketName: {}, objectKey: {}", bucketName, objectKey);

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

    @Override
    public boolean deleteObject(final String bucketName, final String objectKey) {
        LOG.info("deleteObject - bucketName: {}, objectKey: {}", bucketName, objectKey);

        final DeleteObjectRequest deleteBuilder = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        this.s3.deleteObject(deleteBuilder);
        return true;
    }
}
