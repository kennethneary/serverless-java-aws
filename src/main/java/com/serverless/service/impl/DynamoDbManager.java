package com.serverless.service.impl;

import com.serverless.service.DbManager;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

public class DynamoDbManager<T> implements DbManager<T> {

    private final DynamoDbTable<T> table;

    public DynamoDbManager(final String tableName, final Class<T> classType) {
        final DynamoDbEnhancedClient ddbEnhancedClient = DynamoDbEnhancedClient.create();
        final BeanTableSchema<T> beanTableSchema = TableSchema.fromBean(classType);
        final DynamoDbTable<T> table = ddbEnhancedClient.table(tableName, beanTableSchema);
        this.table = table;
    }

    public T getItemById(final String id) {
        final Key key = Key.builder()
                .partitionValue(id)
                .build();
        return this.table.getItem(getItemRequestBuilder -> getItemRequestBuilder.key(key));
    }

    public void save(final T object) { ;
        this.table.putItem(object);
    }

    public T delete(final String id) { ;
        final Key key = Key.builder()
                .partitionValue(id)
                .build();
        return this.table.deleteItem(deleteItemRequestBuilder -> deleteItemRequestBuilder.key(key));
    }

    public List<T> scan() { ;
        return this.table.scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<T> query(final String id) {
        final QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(id)
                .build()
        );
        return this.table.query(queryConditional)
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public T update(final T object) { ;
        return this.table.updateItem(object);
    }
}
