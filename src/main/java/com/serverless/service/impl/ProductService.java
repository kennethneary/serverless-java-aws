package com.serverless.service.impl;

import com.google.inject.Inject;
import com.serverless.Constants;
import com.serverless.model.Content;
import com.serverless.model.Product;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import com.serverless.service.ProductManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductService implements ProductManager {

    private static final Logger LOG = LogManager.getLogger(ProductService.class);

    @Inject
    private ObjectStorageManager osm;

    @Inject
    private DbManager<Product> db;

    public String saveProduct(final Product product) {
        LOG.info("saveProduct - product: " + product);

        final String id = UUID.randomUUID().toString();
        final String timestamp = Instant.now().toString();

        product.setId(id);
        product.setCreatedDateTime(timestamp);
        product.setLastUpdatedDateTime(timestamp);
        this.db.save(product);

        final Content content = product.getContent();
        if (this.isValidS3Body(content)) {
            this.osm.saveObject(Constants.BUCKET_NAME.getValue(), id, content);
        }
        return id;
    }

    public Product getProductById(final String id) throws IOException {
        LOG.info("getProductById - id: " + id);

        final Product product = this.db.getById(id);
        final Content content = this.osm.getObject(Constants.BUCKET_NAME.getValue(), id);
        product.setContent(content);
        return product;
    }

    public void deleteProductById(final String id) {
        LOG.info("deleteProductById - id: " + id);

        this.db.deleteById(id);
        LOG.info("deleteById: " + id);

        LOG.info("Constants.BUCKET_NAME.getValue(): " + Constants.BUCKET_NAME.getValue());
        this.osm.deleteObject(Constants.BUCKET_NAME.getValue(), id);
        LOG.info("deleteObject: " + id);
    }

    public void updateProduct(final String id, final Product product) {
        LOG.info("updateProduct - id: " + id + ", product: " + product);

        final String timestamp = Instant.now().toString();

        product.setId(id);
        product.setLastUpdatedDateTime(timestamp);

        final Content content = product.getContent();
        final String bucketName = Constants.BUCKET_NAME.getValue();
        if (this.isValidS3Body(content)) {
            this.osm.saveObject(bucketName, id, content);
        } else {
            this.osm.deleteObject(bucketName, id);
        }
        this.db.update(product);
    }

    public List<Product> queryProduct(final String id) {
        LOG.info("queryProduct - id: " + id);

        // do not return s3 content. get client to call specific item for performance
        return this.db.query(id);
    }

    public List<Product> getAllProducts() {
        LOG.info("getAllProducts");

        // do not return s3 content. get client to call specific item for performance
        return this.db.scan();
    }

    private boolean isValidS3Body(Content content) {
        LOG.info("isValidS3Body - content: " + content);

        return content != null && StringUtils.isNotBlank(content.getBase64Content())
                && StringUtils.isNotBlank(content.getContentType());
    }
}
