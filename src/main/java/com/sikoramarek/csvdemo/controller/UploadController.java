package com.sikoramarek.csvdemo.controller;

import com.sikoramarek.csvdemo.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@RestController
public class UploadController {

	private UploadService uploadService;

	public UploadController(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	@PostMapping("upload")
	public ResponseEntity upload(@RequestParam("file") MultipartFile multipartFile) {
		return uploadService.parseFile(multipartFile);
	}

	@ExceptionHandler(MultipartException.class)
	ResponseEntity<String> handleBadRequest(HttpServletRequest req, Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), NOT_ACCEPTABLE);
	}

}
