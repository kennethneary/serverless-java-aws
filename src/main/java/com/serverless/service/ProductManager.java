package com.serverless.service;

import com.serverless.model.Product;

import java.io.IOException;
import java.util.List;

public interface ProductManager {
    public String saveProduct(final Product product);
    public Product getProduct(final String id) throws IOException;
    public boolean deleteProduct(final String id);
    public Product updateProduct(final String id, final Product product);
    public List<Product> queryProduct(final String id);
    public List<Product> getAllProducts();
}
