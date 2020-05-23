package com.serverless.service.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.serverless.exception.NotFoundDynamoDbItem;
import com.serverless.service.DbManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.serverless.config.AppModule.*;

@Getter
@Setter
public class DynamoDbManager<T> implements DbManager<T> {

    private static final Logger LOG = LogManager.getLogger(DynamoDbManager.class);

    @Inject
    private DynamoDbTable<T> table;

    @Inject
    @Named(DB_CLASS_TYPE)
    private Class<T> classType;

    @Inject
    @Named(DB_PRIMARY_KEY)
    private String primaryKey;

    public T getById(final String id) {
        LOG.info("getById - id: " + id);

        final Key key = Key.builder()
                .partitionValue(id)
                .build();
        final T item =  this.table.getItem(request -> request.key(key));
        if (Objects.isNull(item)) {
            throw new NotFoundDynamoDbItem();
        }
        return item;
    }

    public void save(final T object) {
        LOG.info("save - object: " + object);

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

    public void deleteById(final String id) {
        LOG.info("deleteById - id: " + id);

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
    }

    public List<T> scan() {
        LOG.info("scan");

        return this.table.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

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

    public void update(final T object) {
        LOG.info("update - object" + object);
        LOG.info("update - this.primaryKey" + this.primaryKey);

        final Expression conditionExpression = Expression.builder()
                .expression("attribute_exists(#primaryKey)")
                .putExpressionName("#primaryKey", this.primaryKey)
                .build();

        final UpdateItemEnhancedRequest<T> updateItemEnhancedRequest = UpdateItemEnhancedRequest.builder(this.classType)
                .item(object)
                .conditionExpression(conditionExpression)
                .build();

        this.table.updateItem(updateItemEnhancedRequest);
    }
}
