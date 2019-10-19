package com.sikoramarek.csvdemo.controller;

import com.sikoramarek.csvdemo.model.UsersData;
import com.sikoramarek.csvdemo.service.UserDataService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class UserDataController {

	public UserDataController(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	private UserDataService userDataService;

	@GetMapping("users")
	public ResponseEntity<Page<UsersData>> getAllUsers() {
		return userDataService.getAllUsers();
	}

	@GetMapping("usersSorted")
	public ResponseEntity<Page<UsersData>> getAllUsersSorted() {
		return userDataService.getAllUsersSorted();
	}
}
