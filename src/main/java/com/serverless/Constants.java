package com.serverless;

public enum  Constants {
    TABLE_NAME(System.getenv("TABLE_NAME"));

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
