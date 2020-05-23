package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.model.ApiGatewayResponse;
import com.serverless.service.ProductManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.serverless.config.AppModule.injector;

public class DeleteProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(DeleteProductHandler.class);

    private ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        try {
            LOG.info("DeleteHandler...");
            final String id = input.getPathParameters().get("id");
            this.productManager.deleteProduct(id);
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .build();
        } catch (Exception ex) {
            LOG.error("Error in deleteing product: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}


