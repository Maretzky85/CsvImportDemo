package com.sikoramarek.csvdemo.service;

import com.sikoramarek.csvdemo.model.UserData;
import com.sikoramarek.csvdemo.repository.UserDataRepository;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;

@CrossOrigin(origins = "*")
@Service
public class UserDataService {

	private UserDataRepository userDataRepository;

	public UserDataService(UserDataRepository userDataRepository) {
		this.userDataRepository = userDataRepository;
	}

	public ResponseEntity<Page<UserData>> getAllUsersSorted(int page, int size) {
		Page<UserData> users = userDataRepository.findAll(PageRequest.of(page, size, Sort.by("birthDate").descending()));
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	public ResponseEntity<Long> countUsers() {
		Long usersCount = userDataRepository.count();
		return new ResponseEntity<>(usersCount, HttpStatus.OK);
	}

	public ResponseEntity<UserData> oldestUserWithPhoneNr() {
		UserData user = userDataRepository.findOldestUserWithPhone();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	public ResponseEntity<List<UserData>> searchBySurname(String search) {
		Optional<List<UserData>> users = userDataRepository.findAllByLastNameLike(search);
		return users.map(userData ->
				new ResponseEntity<>(userData, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));
	}

	public ResponseEntity<UserData> deleteById(Long _id) {
		if (_id >= 0){
			Optional<UserData> user = userDataRepository.findById(_id);
			UserData userData;
			if (user.isPresent()){
				userData = user.get();
				userDataRepository.deleteById(_id);
				return new ResponseEntity<>(userData, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	public ResponseEntity<List> deleteAll() {
		userDataRepository.deleteAll();
		return new ResponseEntity<>(Collections.singletonList("Deleted All Entries"), HttpStatus.OK);
	}
}
