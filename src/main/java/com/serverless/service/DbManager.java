package com.serverless.service;

import java.util.List;

public interface DbManager<T> {
    public T getById(final String id);
    public void save(final T object);
    public boolean delete(final String id);
    public List<T> scan();
    public List<T> query(final String id);
    public T update(final T object);
}
