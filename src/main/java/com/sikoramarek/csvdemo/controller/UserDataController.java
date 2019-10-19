package com.sikoramarek.csvdemo.controller;

import com.sikoramarek.csvdemo.model.UserData;
import com.sikoramarek.csvdemo.service.UserDataService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserDataController {

	private UserDataService userDataService;

	public UserDataController(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	@GetMapping(value = "users", params = {"page", "size"})
	public ResponseEntity<Page<UserData>> getPageableUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
		return userDataService.getAllUsersSorted(page, size);
	}

	@GetMapping("users")
	public ResponseEntity<Page<UserData>> getAllUsers() {
		return userDataService.getAllUsersSorted(0, 5);
	}

	@GetMapping("count")
	public ResponseEntity<Long> getCount() {
		return userDataService.countUsers();
	}

	@GetMapping("getOldest")
	public ResponseEntity<UserData> getOldest() {
		return userDataService.oldestUserWithPhoneNr();
	}

	@GetMapping(value = "search", params = {"search"})
	public ResponseEntity<List<UserData>> searchUserBySurname(@RequestParam("search") String search) {
		return userDataService.searchBySurname(search);
	}
}
