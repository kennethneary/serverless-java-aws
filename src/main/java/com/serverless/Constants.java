package com.serverless;

public enum  Constants {
    TABLE_NAME(System.getenv("PRODUCTS_TABLE_NAME")),
    BUCKET_NAME(System.getenv("PRODUCTS_BUCKET_NAME"));

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
