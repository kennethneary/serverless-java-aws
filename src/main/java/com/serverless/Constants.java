package com.serverless;

public enum  Constants {
    TABLE_NAME(System.getenv("PRODUCTS_TABLE_NAME")),
    TABLE_PRIMARY_ID(System.getenv("PRODUCTS_TABLE_PRIMARY_ID")),
    BUCKET_NAME(System.getenv("PRODUCTS_BUCKET_NAME"));

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
