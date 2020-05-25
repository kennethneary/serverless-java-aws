package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.model.ApiGatewayResponse;
import com.serverless.model.Product;
import com.serverless.model.Response;
import com.serverless.service.ProductManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.serverless.config.AppModule.injector;

public class QueryProductHandler extends BaseEventHandler {

    private static final Logger LOG = LogManager.getLogger(QueryProductHandler.class);

    private final ProductManager productManager = injector.getInstance(ProductManager.class);

    @Override
    public ApiGatewayResponse processEvent(final APIGatewayProxyRequestEvent event, final Context context) {
        LOG.info("QueryHandler...");
        final String name = event.getQueryStringParameters().get("name");
        final List<Product> products = this.productManager.queryProductByName(name);
        final Response response = Response.builder().data(products).build();
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(response)
                .build();
    }
}


