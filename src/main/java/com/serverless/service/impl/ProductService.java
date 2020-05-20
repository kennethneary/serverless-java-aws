package com.serverless.service.impl;

import com.serverless.Constants;
import com.serverless.model.Content;
import com.serverless.model.Product;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import com.serverless.service.ProductManager;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.serverless.config.DependencyInjector.injector;

public class ProductService implements ProductManager {

    private ObjectStorageManager osm = injector.getInstance(ObjectStorageManager.class);
    private DbManager<Product> db = injector.getInstance(DbManager.class);

    public String saveProduct(final Product product) {
        final  String id = UUID.randomUUID().toString();
        product.setId(id);
        this.db.save(product);

        final Content content = product.getContent();
        this.osm.saveObject(Constants.BUCKET_NAME.getValue(), id, content);

        return id;
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
        product.setId(id);
        final Content content = product.getContent();
        this.osm.saveObject(Constants.BUCKET_NAME.getValue(), id, content);
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
