//package com.serverless.handler;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
//import com.serverless.model.ApiGatewayResponse;
//import com.serverless.service.ProductManager;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import static com.serverless.config.AppModule.injector;
//
//public abstract class BaseEventHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {
//
//    private static final Logger LOG = LogManager.getLogger(BaseEventHandler.class);
//
//    public abstract ApiGatewayResponse processEvent(final APIGatewayProxyRequestEvent input, final Context context);
//
//    protected ProductManager productManager = injector.getInstance(ProductManager.class);
//
////    public abstract void injectComponent(final );
//
//
//    @Override
//    public ApiGatewayResponse handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
//        try {
//            return processEvent(input,context);
//        } catch (Exception ex) {
//            LOG.error("Error " + ex);
//            return ApiGatewayResponse.builder()
//                    .setStatusCode(500)
//                    .build();
//        }
//    }
//}
