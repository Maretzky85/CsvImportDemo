package com.sikoramarek.csvdemo.controller;

import com.sikoramarek.csvdemo.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

	public UploadController(UploadService uploadService){
		this.uploadService = uploadService;
	}

	private UploadService uploadService;

	@PostMapping("upload")
	public ResponseEntity upload(@RequestParam("file") MultipartFile multipartFile){
		return uploadService.parseFile(multipartFile);
	}

}
