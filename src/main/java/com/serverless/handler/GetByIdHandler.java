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

import static com.serverless.config.DependencyInjector.injector;

public class GetByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(GetByIdHandler.class);

    private ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            LOG.error("GetByIdHandler...");
            final String id = input.getPathParameters().get("id");
            final Product product = this.productManager.getProduct(id);
            final Response response = Response.builder().data(product).build();
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(response)
                    .build();
        } catch (Exception ex) {
            LOG.error("Error in get by id product: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}


