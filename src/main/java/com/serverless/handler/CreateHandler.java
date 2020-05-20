package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
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

import static com.serverless.config.DependencyInjector.injector;

public class CreateHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(CreateHandler.class);

    private ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            LOG.error("CreateHandler...");
            final Product product = Utils.getObject(input.getBody(), Product.class);
            final String id = this.productManager.saveProduct(product);
            final Map<String, Object> data = ImmutableMap.<String, Object>builder()
                    .put("id", id)
                    .build();
            final Response response = Response.builder().data(data).build();
            return ApiGatewayResponse.builder()
                    .setStatusCode(201)
                    .setObjectBody(response)
                    .build();
        } catch (Exception ex) {
            LOG.error("Error in saving product: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}


