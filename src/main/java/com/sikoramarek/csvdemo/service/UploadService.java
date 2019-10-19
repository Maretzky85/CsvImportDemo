package com.sikoramarek.csvdemo.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.sikoramarek.csvdemo.controller.UploadController;
import com.sikoramarek.csvdemo.error.ParseError;
import com.sikoramarek.csvdemo.model.UploadResponse;
import com.sikoramarek.csvdemo.model.UserData;
import com.sikoramarek.csvdemo.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

	public ResponseEntity<UploadResponse> parseFile(MultipartFile multipartFile) {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
		MappingIterator<Map<String, String>> data;
		try {
			data = mapper.readerFor(Map.class)
					.with(schema)
					.readValues(multipartFile.getInputStream());
		} catch (IOException e) {
			logger.error("Error parsing file" + e.toString());
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		return parseData(data);
	}

	private ResponseEntity<UploadResponse> parseData(MappingIterator<Map<String, String>> data) {
		UploadResponse uploadResponse = new UploadResponse();
		while (data.hasNext()) {
			Map<String, String> singleRowData = data.next();
			try {
				parseIdentity(singleRowData, uploadResponse);
				uploadResponse.addAccepted();
			} catch (ParseError e) {
				logger.error(e.getMessage() + " - caused by: " + singleRowData.toString());
			}
		}
		return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
	}

	private void parseIdentity(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		String phoneNo = phoneNoCheck(singleRowData, uploadResponse);
		String[] names = namesCheck(singleRowData, uploadResponse);
		String date = dateFormatCheck(singleRowData, uploadResponse);
		String firstName = names[0];
		String lastName = names[1];
		try {
			UserData user = UserData.builder()
					.firstName(firstName)
					.lastName(lastName)
					.birthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")))
					.build();
			if (phoneNo != null && phoneNo.length() == 9) {
				user.setPhoneNo(phoneNo);
			}
			usersDataRepository.save(user);
		} catch (DateTimeParseException e) {
			logger.error(e.toString() + " - caused by: " + singleRowData.toString());
			throw new ParseError(e.toString());
		}
	}

	private String[] namesCheck(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		String firstName = singleRowData.getOrDefault("first_name", "");
		String lastName = singleRowData.getOrDefault("last_name", "");
		if (StringUtils.trimAllWhitespace(firstName).length() < 2 ||
				StringUtils.trimAllWhitespace(lastName).length() < 2) {
			String error = "First name or last name error";
			uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
			throw new ParseError(error);
		}
		return new String[]{firstName, lastName};
	}

	private String phoneNoCheck(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		String phoneNo = singleRowData.getOrDefault("phone_no", "0");
		phoneNo = StringUtils.trimAllWhitespace(phoneNo);
		if (phoneNo.length() == 9) {
			if (usersDataRepository.findByPhoneNoEquals(phoneNo) != null) {
				String error = "Phone number already exists in database";
				uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
				throw new ParseError(error);
			}
			return phoneNo;
		}
		if (phoneNo.length() < 2) {
			return null;
		}
		String error = "Error parsing phone number ";
		uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
		throw new ParseError(error);
	}

	private String dateFormatCheck(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		String birthday = singleRowData.getOrDefault("birth_date", "");
		if (birthday != null && StringUtils.trimAllWhitespace(birthday).length() > 6) {
			String[] date = StringUtils.trimAllWhitespace(birthday).split("\\.");
			StringBuilder sb = new StringBuilder();
			if (date.length == 3 && date[0].length() == 4) {
				sb.append(date[0]);
				if (date[1].length() == 1) {
					date[1] = "0" + date[1];
				}
				sb.append(date[1]);
				if (date[1].length() == 1) {
					date[2] = "0" + date[2];
				}
				sb.append(date[1]);
				return sb.toString();
			}
		}
		String error = "Error parsing birth date ";
		throw new ParseError(error);
	}
}
