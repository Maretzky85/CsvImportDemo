package com.sikoramarek.csvdemo.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.sikoramarek.csvdemo.controller.UploadController;
import com.sikoramarek.csvdemo.model.UploadResponse;
import com.sikoramarek.csvdemo.model.UserData;
import com.sikoramarek.csvdemo.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class UploadService {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	private UserDataRepository usersDataRepository;

	public UploadService(UserDataRepository usersDataRepository) {
		this.usersDataRepository = usersDataRepository;
	}

	public ResponseEntity parseFile(MultipartFile multipartFile) {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
		MappingIterator<Map<String, String>> data;
		try {
			data = mapper.readerFor(Map.class)
					.with(schema)
					.readValues(multipartFile.getInputStream());
		} catch (IOException e) {
			logger.error("Error parsing file" + e.toString());
			return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
		}
		return parseData(data);
	}

	private ResponseEntity<UploadResponse> parseData(MappingIterator<Map<String, String>> data) {
		UploadResponse uploadResponse = new UploadResponse();
		while (data.hasNext()) {
			Map<String, String> singleRowData = data.next();
			if (parseIdentity(singleRowData, uploadResponse)) {
				uploadResponse.addAccepted();
			} else {
				uploadResponse.addFailed();
			}
		}
		return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
	}

	private boolean parseIdentity(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		String firstName;
		String lastName;
		String date;
		try {
			firstName = singleRowData.get("first_name").replaceAll("\\s", "");
			lastName = singleRowData.get("last_name").replaceAll("\\s", "");
			date = checkDateFormat(singleRowData.get("birth_date"));
		} catch (NullPointerException e) {
			String error = "All fields must be filled in";
			uploadResponse.addToFailedWithCouse(singleRowData.toString(), error);
			logger.error(error);
			return false;
		}
		int size = singleRowData.size();
		if (size < 3 || date == null || firstName.length() < 3 || lastName.length() < 3) {
			String error = "First name, last name and birth date must by filled in";
			uploadResponse.addToFailedWithCouse(singleRowData.toString(), error);
			logger.error("Error parsing " + singleRowData.toString());
			return false;
		}
		try {
			UserData user = UserData.builder()
					.firstName(firstName)
					.lastName(lastName)
					.birthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")))
					.build();
			if (size == 4) {
				String phone_no = singleRowData.get("phone_no");
				if (phone_no.length() == 0) {

				} else if (phone_no.length() != 9) {
					String error = "Phone number in wrong format";
					uploadResponse.addToFailedWithCouse(singleRowData.toString(), error);
					logger.error("Phone number in wrong format" + singleRowData);
				} else {
					if (usersDataRepository.findByPhoneNoEquals(phone_no) != null) {
						String error = "User with this phone nr exists ";
						uploadResponse.addToFailedWithCouse(singleRowData.toString(), error);
						logger.error(error + singleRowData);
						return false;
					}
					user.setPhoneNo(phone_no);
				}
			}
			usersDataRepository.save(user);
		} catch (DateTimeParseException e) {
			logger.error(e.toString());
			return false;
		}
		return true;
	}

	private String checkDateFormat(String birthday) {
		if (birthday != null && birthday.trim().length() > 6) {
			String[] date = birthday.trim().split("\\.");
			StringBuilder sb = new StringBuilder();
			if (date.length == 3) {
				assert (date[0].length() == 4);
				sb.append(date[0]);
				if (date[1].length() == 1) {
					date[1] = "0" + date[1];
				}
				sb.append(date[1]);
				if (date[1].length() == 1) {
					date[2] = "0" + date[2];
				}
				sb.append(date[1]);
			}
			return sb.toString();
		} else {
			logger.error("error parsing birth date " + birthday);
		}
		return null;
	}
}
