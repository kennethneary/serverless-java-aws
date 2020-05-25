package com.serverless;

public enum Constants {
    PRODUCTS_TABLE_NAME(System.getenv("PRODUCTS_TABLE_NAME")),
    PRODUCTS_TABLE_PRIMARY_ID(System.getenv("PRODUCTS_TABLE_PRIMARY_ID")),
    PRODUCTS_TABLE_SECONDARY_INDEX(System.getenv("PRODUCTS_TABLE_SECONDARY_INDEX")),
    PRODUCTS_BUCKET_NAME(System.getenv("PRODUCTS_BUCKET_NAME"));

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
