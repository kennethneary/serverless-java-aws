package com.serverless.service;

import java.util.List;

public interface DbManager<T> {
    public T getById(final String id);
    public void save(final String partitionKey, final T object);
    public void deleteById(final String partitionKey, final String id);
    public List<T> scan();
    public List<T> query(final String id);
    public List<T> queryGSI(final String indexName, final String id);
    public void update(final String partitionKey, final T object);
}
