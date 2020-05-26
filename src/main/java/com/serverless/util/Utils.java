package com.serverless.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Utils {
    private static final Logger LOG = LogManager.getLogger(Utils.class);

    private static ObjectMapper getObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

    public static <T> T getObject(String json, Class<T> classType) {
        try {
            return getObjectMapper().readValue(json, classType);
        } catch(IOException e) {
            LOG.error("Marshalling error: {}", e);
        }
        return null;
    }
}
