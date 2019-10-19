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

	public void addToFailedWithCouse(String key, String value) {
		failedList.put(key, value);
	}

}
