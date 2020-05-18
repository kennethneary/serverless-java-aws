package com.serverless.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Getter
@Setter
@Builder
public class Content {
    private String base64Content;
    private String contentType;
}
