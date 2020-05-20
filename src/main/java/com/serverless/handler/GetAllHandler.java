package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.model.ApiGatewayResponse;
import com.serverless.model.Product;
import com.serverless.model.Response;
import com.serverless.service.ProductManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.serverless.config.DependencyInjector.injector;

public class GetAllHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(GetAllHandler.class);

    private ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            final List<Product> products = this.productManager.getAllProducts();
            final Response response = Response.builder().data(products).build();
            return ApiGatewayResponse.builder()
                    .setStatusCode(201)
                    .setObjectBody(response)
                    .build();
        } catch (Exception ex) {
            LOG.error("Error in get all product: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}


