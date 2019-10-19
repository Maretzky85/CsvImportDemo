package com.sikoramarek.csvdemo.service;

import com.sikoramarek.csvdemo.model.UsersData;
import com.sikoramarek.csvdemo.repository.UsersDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserDataService {

	UsersDataRepository usersDataRepository;

	public UserDataService(UsersDataRepository usersDataRepository){
		this.usersDataRepository = usersDataRepository;
		UsersData user = UsersData.builder()
				.first_name("Stefan")
				.last_name("Testowy")
				.birthDate(LocalDate.parse("1988.11.11", DateTimeFormatter.ofPattern("yyyy.MM.dd")))
				.phone_no("600700800")
				.build();
		usersDataRepository.save(user);
	}

	public ResponseEntity<Page<UsersData>> getAllUsers() {
		Page<UsersData> users = usersDataRepository.findAll(PageRequest.of(0, 5));
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	public ResponseEntity<Page<UsersData>> getAllUsersSorted() {
		Page<UsersData> users = usersDataRepository.findAll(PageRequest.of(0, 5, Sort.by("birthDate").descending()));
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

}
