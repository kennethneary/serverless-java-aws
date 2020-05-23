package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.model.ApiGatewayResponse;
import com.serverless.model.Product;
import com.serverless.service.ProductManager;
import com.serverless.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.serverless.config.AppModule.injector;

public class UpdateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(UpdateProductHandler.class);

    private ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        try {
            LOG.info("UpdateHandler...");
            final String id = input.getPathParameters().get("id");
            final Product product = Utils.getObject(input.getBody(), Product.class);
            this.productManager.updateProduct(id, product);
            return ApiGatewayResponse.builder()
                    .setStatusCode(201)
                    .build();
        } catch (Exception ex) {
            LOG.error("Error in updating product: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}


