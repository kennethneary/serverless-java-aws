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
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductService implements ProductManager {

    @Inject
    private ObjectStorageManager osm;

    @Inject
    private DbManager<Product> db;

    public String saveProduct(final Product product) {
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

    private boolean isValidS3Body(Content content) {
        return content != null && StringUtils.isNotBlank(content.getBase64Content())
                && StringUtils.isNotBlank(content.getContentType());
    }

    public Product getProduct(final String id) throws IOException {
        final Product product = this.db.getById(id);
        final Content content = this.osm.getObject(Constants.BUCKET_NAME.getValue(), id);
        product.setContent(content);
        return product;
    }

    public boolean deleteProduct(final String id) {
        this.db.delete(id);
        return this.osm.deleteObject(Constants.BUCKET_NAME.getValue(), id);
    }

    public Product updateProduct(final String id, final Product product) {
        final String timestamp = Instant.now().toString();

        product.setId(id);
        product.setLastUpdatedDateTime(timestamp);

        final Content content = product.getContent();
        if (this.isValidS3Body(content)) {
            this.osm.saveObject(Constants.BUCKET_NAME.getValue(), id, content);
        }
        return this.db.update(product);
    }

    public List<Product> queryProduct(final String id) {
        // do not return s3 content. get client to call specific item for performance
        return this.db.query(id);
    }

    public List<Product> getAllProducts() {
        // do not return s3 content. get client to call specific item for performance
        return this.db.scan();
    }
}
