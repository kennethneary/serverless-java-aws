package com.serverless.service.impl;

import com.serverless.service.DbManager;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class DynamoDbManager<T> implements DbManager<T> {

    private final DynamoDbTable<T> table;
    private final Class<T> classType;
    private final String primaryKey;

    public DynamoDbManager(final String tableName, final Class<T> classType, final String primaryKey) {
        final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();
        final BeanTableSchema<T> beanTableSchema = TableSchema.fromBean(classType);
        this.table = ddbEnhancedClient.table(tableName, beanTableSchema);
        this.classType = classType;
        this.primaryKey = primaryKey;
    }

    public T getById(final String id) {
        final Key key = Key.builder()
                .partitionValue(id)
                .build();
        return this.table.getItem(request -> request.key(key));
    }

    public void save(final T object) {
        final Expression conditionExpression = Expression.builder()
                .expression("attribute_not_exists(#primaryKey)")
                .putExpressionName("#primaryKey", this.primaryKey)
                .build();

        final PutItemEnhancedRequest<T> putItemEnhancedRequest = PutItemEnhancedRequest.builder(this.classType)
                .item(object)
                .conditionExpression(conditionExpression)
                .build();

        this.table.putItem(putItemEnhancedRequest);
    }

    public boolean delete(final String id) {
        final Key key = Key.builder()
                .partitionValue(id)
                .build();

        final Expression conditionExpression = Expression.builder()
                .expression("attribute_exists(#primaryKey)")
                .putExpressionName("#primaryKey", this.primaryKey)
                .build();

        final DeleteItemEnhancedRequest deleteItemEnhancedRequest = DeleteItemEnhancedRequest.builder()
                .key(key)
                .conditionExpression(conditionExpression)
                .build();

        this.table.deleteItem(deleteItemEnhancedRequest);
        return true;
    }

    public List<T> scan() {
        return this.table.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<T> query(final String id) {
        final Key key = Key.builder()
                .partitionValue(id)
                .build();

        final QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        final QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return this.table.query(queryEnhancedRequest)
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public T update(final T object) {
        final Expression conditionExpression = Expression.builder()
                .expression("attribute_exists(#primaryKey)")
                .putExpressionName("#primaryKey", this.primaryKey)
                .build();

        final UpdateItemEnhancedRequest<T> updateItemEnhancedRequest = UpdateItemEnhancedRequest.builder(this.classType)
                .item(object)
                .conditionExpression(conditionExpression)
                .build();

        return this.table.updateItem(updateItemEnhancedRequest);
    }
}
