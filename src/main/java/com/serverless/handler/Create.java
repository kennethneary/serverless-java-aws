package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.model.ApiGatewayResponse;
import com.serverless.model.Product;
import com.serverless.service.DbManager;
import com.serverless.service.ProductManager;
import com.serverless.service.impl.ProductService;
import com.serverless.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.serverless.config.DependencyInjector.injector;

public class Create implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(Handler.class);

    private ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            final Product product = Utils.getObject(input.getBody(), Product.class);
            final String id = productManager.saveProduct(product);

            final Product response = Product.builder().id(id).build();
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


