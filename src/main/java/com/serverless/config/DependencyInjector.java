package com.serverless.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import com.serverless.service.ProductManager;
import com.serverless.service.impl.DynamoDbManager;
import com.serverless.service.impl.ObjectStorageService;
import com.serverless.service.impl.ProductService;

public class DependencyInjector extends AbstractModule {

    public static final Injector injector = Guice.createInjector(new DependencyInjector());

    @Override
    protected void configure() {
        bind(DbManager.class).to(DynamoDbManager.class);
        bind(ProductManager.class).to(ProductService.class);
        bind(ObjectStorageManager.class).to(ObjectStorageService.class);
        bind(ProductManager.class).to(ProductService.class);
    }
}
