package com.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.model.ApiGatewayResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseEventHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(BaseEventHandler.class);

    public abstract ApiGatewayResponse processEvent(final APIGatewayProxyRequestEvent event, final Context context) throws Exception;

    @Override
    public ApiGatewayResponse handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        try {
            LOG.error("Processing Event...");
            return processEvent(event, context);
        } catch (Exception ex) {
            LOG.error("Error: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .build();
        }
    }
}
