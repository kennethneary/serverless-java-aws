package com.serverless.service.impl;

import com.google.inject.Inject;
import com.serverless.model.Content;
import com.serverless.model.Product;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import com.serverless.service.ProductManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.serverless.Constants.*;

@Getter
@Setter
public class ProductService implements ProductManager {

    private static final Logger LOG = LogManager.getLogger(ProductService.class);

    @Inject
    private ObjectStorageManager osm;

    @Inject
    private DbManager<Product> db;

    @Override
    public String saveProduct(final Product product) {
        LOG.info("saveProduct - product: {}", product);

        final String id = UUID.randomUUID().toString();
        final String timestamp = Instant.now().toString();

        product.setId(id);
        product.setCreatedDateTime(timestamp);
        product.setLastUpdatedDateTime(timestamp);
        this.db.save(PRODUCTS_TABLE_PRIMARY_ID.getValue(), product);

        final Content content = product.getContent();
        if (this.isValidS3Body(content)) {
            this.osm.saveObject(PRODUCTS_BUCKET_NAME.getValue(), id, content);
        }
        return id;
    }

    @Override
    public Product getProductById(final String id) throws IOException {
        LOG.info("getProductById - id: {}", id);

        final Product product = this.db.getById(id);
        try {
            final Content content = this.osm.getObject(PRODUCTS_BUCKET_NAME.getValue(), id);
            product.setContent(content);
        } catch (S3Exception s3e) {
            LOG.error("S3Exception: {}", s3e);
            if (s3e.statusCode() != 404) throw s3e;
        }
        return product;
    }

    @Override
    public void deleteProductById(final String id) {
        LOG.info("deleteProductById - id: {}", id);

        this.db.deleteById(PRODUCTS_TABLE_PRIMARY_ID.getValue(), id);

        // there may not be an object associated with the DynamoDb entry which is valid
        try {
            this.osm.deleteObject(PRODUCTS_BUCKET_NAME.getValue(), id);
        } catch (S3Exception s3e) {
            LOG.error("S3Exception: {}", s3e);
            if (s3e.statusCode() != 404) throw s3e;
        }
    }

    @Override
    public void updateProduct(final String id, final Product product) {
        LOG.info("updateProduct - id: {}, product: {}", id, product);

        product.setId(id);
        this.db.update(PRODUCTS_TABLE_PRIMARY_ID.getValue(), product);

        final Content content = product.getContent();
        final String bucketName = PRODUCTS_BUCKET_NAME.getValue();
        if (this.isValidS3Body(content)) {
            this.osm.saveObject(bucketName, id, content);
        } else {
            this.osm.deleteObject(bucketName, id);
        }
    }

    @Override
    public List<Product> queryProductByName(final String name) {
        LOG.info("queryProduct - name: {}", name);

        // do not return s3 content. get client to call specific item for performance
        return this.db.queryGSI(PRODUCTS_TABLE_SECONDARY_INDEX.getValue(), name);
    }

    @Override
    public List<Product> getAllProducts() {
        LOG.info("getAllProducts");

        // do not return s3 content. get client to call specific item for performance
        return this.db.scan();
    }

    private boolean isValidS3Body(Content content) {
        LOG.info("isValidS3Body - content: {}", content);

        return content != null && StringUtils.isNotBlank(content.getBase64Content())
                && StringUtils.isNotBlank(content.getContentType());
    }
}
