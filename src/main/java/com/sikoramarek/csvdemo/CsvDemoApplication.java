package com.sikoramarek.csvdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsvDemoApplication {

	/**
	 * Simple application for uploading csv and storing data in H2 in memory database
	 * CSV format:
	 * * first column must be: 'first_name;last_name;birth_date;phone_no'
	 * * other columns as data : name;last name;yyyy.mm.dd;000000000000
	 * first and last name max size - 50 characters
	 * date format - year(4 digits):month(2 digits):day(2 digits)
	 *
	 * Available endpoints (JSON):
	 *      /upload
	 *          - methods: POST
	 *          - form-data
	 *          -- uploads one file and fetches data to database
	 *          -- returns summary of data and errors
	 *      /users
	 *          - methods: GET
	 *          - params accepted: page, size (both or none)
	 *          - returns pageable user data results from database
	 *      /oldest
	 *          - methods: GET
	 *          - returns oldest person with provided correct phone number data
	 *      /search
	 *          - methods: GET
	 *          - params: search
	 *          - returns list of userdata where search matches surname (partially or fully)
	 *      /delete
	 *          - methods: POST
	 *          - params: id
	 *          - deletes user of id provided in params
	 *          - returns data of deleted user or not found response
	 *      /deleteAll
	 *          - methods: POST
	 *          - deletes all entries in database
	 *          - returns ok status
	 */

	public static void main(String[] args) {
		SpringApplication.run(CsvDemoApplication.class, args);
	}

}
