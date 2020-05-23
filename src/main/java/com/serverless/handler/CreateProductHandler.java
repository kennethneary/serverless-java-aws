package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.common.collect.ImmutableMap;
import com.serverless.model.ApiGatewayResponse;
import com.serverless.model.Product;
import com.serverless.model.Response;
import com.serverless.service.ProductManager;
import com.serverless.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static com.serverless.config.AppModule.injector;

public class CreateProductHandler extends BaseEventHandler {

    private static final Logger LOG = LogManager.getLogger(CreateProductHandler.class);

    private final ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse processEvent(final APIGatewayProxyRequestEvent event, final Context context) {
        LOG.info("CreateHandler...");
        final Product product = Utils.getObject(event.getBody(), Product.class);
        LOG.info("CreateHandler after product const...");
        final String id = this.productManager.saveProduct(product);
        final Map<String, Object> data = ImmutableMap.<String, Object>builder()
                .put("id", id)
                .build();
        final Response response = Response.builder().data(data).build();
        return ApiGatewayResponse.builder()
                .setStatusCode(201)
                .setObjectBody(response)
                .build();
    }
}


