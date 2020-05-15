package com.serverless.model;

import lombok.Data;

import java.util.Map;

@Data
public class Response {
	private Map<String, Object> data;
}
