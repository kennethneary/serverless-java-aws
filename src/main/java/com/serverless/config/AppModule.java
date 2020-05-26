package com.serverless.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.serverless.Constants;
import com.serverless.model.Product;
import com.serverless.service.DbManager;
import com.serverless.service.ObjectStorageManager;
import com.serverless.service.ProductManager;
import com.serverless.service.impl.DynamoDbService;
import com.serverless.service.impl.ObjectStorageService;
import com.serverless.service.impl.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.services.s3.S3Client;

public class AppModule extends AbstractModule {

    private static final Logger LOG = LogManager.getLogger(AppModule.class);

    public static final Injector injector = Guice.createInjector(new AppModule());
    public static final String DB_CLASS_TYPE = "classType";

    @Override
    protected void configure() {
        LOG.error("AppModule configure:");

        bind(ProductManager.class).to(ProductService.class);
        bind(ObjectStorageManager.class).to(ObjectStorageService.class);
        bind(S3Client.class).toInstance(S3Client.create());

        bind(new TypeLiteral<Class<Product>>(){}).annotatedWith(Names.named(DB_CLASS_TYPE)).toInstance(Product.class);
        bind(new TypeLiteral<DynamoDbTable<Product>>(){}).toInstance(createDynamoDbTableClient(Product.class));
        bind(new TypeLiteral<DbManager<Product>>(){}).to(new TypeLiteral<DynamoDbService<Product>>(){});
    }

    private static <T> DynamoDbTable<T> createDynamoDbTableClient(final Class<T> classType) {
        final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();
        final BeanTableSchema<T> beanTableSchema = TableSchema.fromBean(classType);
        final DynamoDbTable<T> dynamoDbTable = ddbEnhancedClient.table(Constants.PRODUCTS_TABLE_NAME.getValue(), beanTableSchema);
        return dynamoDbTable;
    }
}
