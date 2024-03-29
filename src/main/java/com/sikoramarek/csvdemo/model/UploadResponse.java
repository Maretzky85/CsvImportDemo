package com.sikoramarek.csvdemo.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UploadResponse {

	int total;
	int accepted;
	int failed;
	Map<String, String> failedList;

	public UploadResponse() {
		failedList = new HashMap<>();
	}

	public void addAccepted() {
		total++;
		accepted++;
	}

	public void addFailed() {
		total++;
		failed++;
	}

	public void addToFailedWithCause(String key, String value) {
		total++;
		failed++;
		failedList.put("Input", key);
		failedList.put("Error", value);
	}

}
