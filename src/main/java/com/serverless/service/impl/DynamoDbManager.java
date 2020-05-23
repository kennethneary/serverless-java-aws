package com.serverless.service.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.serverless.service.DbManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.serverless.config.AppModule.*;

@Getter
@Setter
public class DynamoDbManager<T> implements DbManager<T> {

    @Inject
    private DynamoDbTable<T> table;

    @Inject
    @Named(DB_CLASS_TYPE)
    private Class<T> classType;

    @Inject
    @Named(DB_PRIMARY_KEY)
    private String primaryKey;

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
