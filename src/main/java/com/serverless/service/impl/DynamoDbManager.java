package com.serverless.service.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.serverless.service.DbManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.stream.Collectors;

import static com.serverless.config.AppModule.DB_CLASS_TYPE;

@Getter
@Setter
public class DynamoDbManager<T> implements DbManager<T> {

    private static final Logger LOG = LogManager.getLogger(DynamoDbManager.class);

    @Inject
    private DynamoDbTable<T> table;

    @Inject
    @Named(DB_CLASS_TYPE)
    private Class<T> classType;

    @Override
    public T getById(final String id) {
        LOG.info("getById - id: " + id);

        final Key key = Key.builder()
                .partitionValue(id)
                .build();
        return this.table.getItem(request -> request.key(key));
    }

    @Override
    public void save(final String partitionKey, final T object) {
        LOG.info("save - partitionKey: " + partitionKey + ", object: " + object);

        final Expression conditionExpression = Expression.builder()
                .expression("attribute_not_exists(#primaryKey)")
                .putExpressionName("#primaryKey", partitionKey)
                .build();

        final PutItemEnhancedRequest<T> putItemEnhancedRequest = PutItemEnhancedRequest.builder(this.classType)
                .item(object)
                .conditionExpression(conditionExpression)
                .build();

        this.table.putItem(putItemEnhancedRequest);
    }

    @Override
    public void deleteById(final String partitionKey, final String id) {
        LOG.info("deleteById - partitionKey: " + partitionKey + ", id: " + id);

        final Key key = Key.builder()
                .partitionValue(id)
                .build();

        // bug in sdk for delete
        // if has expression without expressionNames or expressionValue will cause null pointer exception
        final Expression conditionExpression = Expression.builder()
                .expression("attribute_exists(#primaryKey) AND #primaryKey <> :empty")
                .putExpressionName("#primaryKey", partitionKey)
                .putExpressionValue(":empty", AttributeValue.builder().s("").build())
                .build();

        final DeleteItemEnhancedRequest deleteItemEnhancedRequest = DeleteItemEnhancedRequest.builder()
                .key(key)
                .conditionExpression(conditionExpression)
                .build();

        this.table.deleteItem(deleteItemEnhancedRequest);
    }

    @Override
    public List<T> scan() {
        LOG.info("scan");

        return this.table.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<T> query(final String id) {
        LOG.info("query - id" + id);

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

    @Override
    public List<T> queryGSI(final String indexName, final String id) {
        LOG.info("query - id" + id);

        final Key key = Key.builder()
                .partitionValue(id)
                .build();

        final QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        final QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return this.table.index(indexName)
                .query(queryEnhancedRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void update(final String partitionKey, final T object) {
        LOG.info("update - partitionKey: " + partitionKey + ", object: " + object);

        final Expression conditionExpression = Expression.builder()
                .expression("attribute_exists(#primaryKey)")
                .putExpressionName("#primaryKey", partitionKey)
                .build();

        final UpdateItemEnhancedRequest<T> updateItemEnhancedRequest = UpdateItemEnhancedRequest.builder(this.classType)
                .item(object)
                .conditionExpression(conditionExpression)
                .build();

        this.table.updateItem(updateItemEnhancedRequest);
    }
}
