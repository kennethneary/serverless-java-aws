package com.serverless.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.serverless.Constants;
import com.serverless.model.Product;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import com.serverless.service.ProductManager;
import com.serverless.service.impl.DynamoDbManager;
import com.serverless.service.impl.ObjectStorageService;
import com.serverless.service.impl.ProductService;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.services.s3.S3Client;

public class AppModule extends AbstractModule {

    public static final Injector injector = Guice.createInjector(new AppModule());

    @Override
    protected void configure() {
        bind(ProductManager.class).to(ProductService.class);
        bind(ObjectStorageManager.class).to(ObjectStorageService.class);
        bind(S3Client.class).toInstance(S3Client.create());

        bind(DbManager.class).to(DynamoDbManager.class);

        bind(Class.class).annotatedWith(Names.named("classType")).toInstance(Product.class);
        bind(String.class).annotatedWith(Names.named("primaryKey")).toInstance("id");
        bind(DynamoDbTable.class).toInstance(createProductDynamoDbTable(Product.class));
    }

    private static <T> DynamoDbTable<T> createProductDynamoDbTable(final Class<T> classType) {
        final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();
        final BeanTableSchema<T> beanTableSchema = TableSchema.fromBean(classType);
        final DynamoDbTable<T> dynamoDbTable = ddbEnhancedClient.table(Constants.TABLE_NAME.getValue(), beanTableSchema);
        return dynamoDbTable;
    }
}
