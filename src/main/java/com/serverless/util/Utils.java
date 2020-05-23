package com.serverless.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;

public class Utils {
    private static final Logger LOG = LogManager.getLogger(Utils.class);

    private static ObjectMapper getObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        return mapper;
    }

    public static <T> T getObject(String json, Class<T> classType) {
        try {
            return getObjectMapper().readValue(json, classType);
        } catch(IOException e) {
            LOG.error("Marshalling error: " + e);
        }
        return null;
    }

//    public static void main(String ...json) {
////        Product p = getObject("{\n" +
////                "    \"id\": \"id\",\n" +
////                "    \"name\": \"john\",\n" +
////                "    \"price\": \"price\",\n" +
////                "    \"description\": \"description\",\n" +
////                "    \"createdDateTime\": \"createdDateTime\",\n" +
////                "    \"lastUpdatedDateTime\": \"lastUpdatedDateTime\",\n" +
////                "    \"content\": {\n" +
////                "        \"base64Content\": \"base64Content\",\n" +
////                "        \"contentType\": \"contentType\"\n" +
////                "    }\n" +
////                "}", Product.class);
////        System.out.println(p);
//
//        Instant instant = Instant.now();
//        System.out.println(instant.toString());
//
//    }
}
