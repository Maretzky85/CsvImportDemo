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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class UploadService {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	private final int PHONE_NUMBER_LENGTH = 9;
	private final int PHONE_NUMBER_ERROR_THRESHOLD = 1;
	private UserDataRepository usersDataRepository;

	public UploadService(UserDataRepository usersDataRepository) {
		this.usersDataRepository = usersDataRepository;
	}

	/**
	 * Creates CsvMapper and tries to map key-value pairs
	 * sends map to parseData method on success
	 * or else returns NOT_ACCEPTABLE response
	 *
	 * @param multipartFile - uploaded file
	 * @return NOT_ACCEPTABLE response or UploadResponse
	 */
	public ResponseEntity<UploadResponse> loadData(MultipartFile multipartFile) {
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

	/**
	 * Helper method for parsing data
	 * Creates UploadResponse entity for collecting stats for individual entries
	 * using MappingIterator sends every single row data to parseIdentity method
	 *
	 * @param data - MappingIterator holding all parsed data from csv file
	 * @return uploadResponse entity wrapped by ResponseEntity class
	 * populated by either helper methods or this method
	 */
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

	/**
	 * Helper method parsing, preparing and eventually saving single entity data
	 *
	 * @param singleRowData  - Key-Value Map with single entry data
	 * @param uploadResponse - uploadResponse entity created by parseData for status report
	 */

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
			if (phoneNo != null && phoneNo.length() == PHONE_NUMBER_LENGTH) {
				user.setPhoneNo(phoneNo);
			}
			usersDataRepository.save(user);
		} catch (DateTimeParseException e) {
			logger.error(e.toString() + " - caused by: " + singleRowData.toString());
			throw new ParseError(e.toString());
		}
	}

	/**
	 * Utility method for checking correctness of first and last name
	 *
	 * @param singleRowData  - Key-Value Map with single entry data
	 * @param uploadResponse - uploadResponse entity created by parseData for status report
	 * @return String Array of length 2, where first position is first name
	 * and second position last name
	 * Or updates uploadResponse and throws ParseError
	 */
	private String[] namesCheck(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		int NAME_LENGTH_MIN = 2;
		String firstName = singleRowData.getOrDefault("first_name", "").replaceAll("\\s", "");
		String lastName = singleRowData.getOrDefault("last_name", "").replaceAll("\\s", "");
		if (firstName.length() < NAME_LENGTH_MIN ||
				lastName.length() < NAME_LENGTH_MIN) {
			String error = "First name or last name error";
			uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
			throw new ParseError(error);
		}
		return new String[]{firstName, lastName};
	}

	/**
	 * Utility method for checking correctness of phone number
	 *
	 * @param singleRowData  - Key-Value Map with single entry data
	 * @param uploadResponse - uploadResponse entity created by parseData for status report
	 * @return String with correct phone number or null
	 * Or updates uploadResponse and throws ParseError
	 */
	private String phoneNoCheck(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		String phoneNo = singleRowData.getOrDefault("phone_no", "0");
		phoneNo = phoneNo.replaceAll("\\s", "");
		if (phoneNo.length() == PHONE_NUMBER_LENGTH) {
			if (usersDataRepository.findByPhoneNoEquals(phoneNo) != null) {
				String error = "Phone number already exists in database";
				uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
				throw new ParseError(error);
			}
			return phoneNo;
		}
		if (phoneNo.length() <= PHONE_NUMBER_ERROR_THRESHOLD) {
			return null;
		}
		String error = "Error parsing phone number ";
		uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
		throw new ParseError(error);
	}

	/**
	 * Utility method for checking correctness of birth date
	 *
	 * @param singleRowData  - Key-Value Map with single entry data
	 * @param uploadResponse - uploadResponse entity created by parseData for status report
	 * @return String containing formatted date
	 * Or updates uploadResponse and throws ParseError
	 */
	private String dateFormatCheck(Map<String, String> singleRowData, UploadResponse uploadResponse) {
		int YEAR_INDEX = 0;
		int MONTH_INDEX = 1;
		int DAY_INDEX = 2;
		int YEAR_STRING_LENGTH = 4;
		int BIRTHDAY_STRING_LENGTH_MIN = 7;
		int DATE_ELEMENTS = 3;

		String birthday = singleRowData.getOrDefault("birth_date", "");
		if (birthday != null && birthday.replaceAll("\\s", "").length() > BIRTHDAY_STRING_LENGTH_MIN) {
			String[] date = birthday.replaceAll("\\s", "").split("\\.");
			StringBuilder sb = new StringBuilder();
			if (date.length == DATE_ELEMENTS && date[YEAR_INDEX].length() == YEAR_STRING_LENGTH) {
				sb.append(date[YEAR_INDEX]);
				sb.append(checkAndAddMissingZero(date[MONTH_INDEX]));
				sb.append(checkAndAddMissingZero(date[DAY_INDEX]));
				return sb.toString();
			}
		}
		String error = "Error parsing birth date ";
		uploadResponse.addToFailedWithCause(singleRowData.toString(), error);
		throw new ParseError(error);
	}

	private String checkAndAddMissingZero(String toCheck) {
		if (toCheck.length() == 1) {
			return "0" + toCheck;
		}
		return toCheck;
	}
}
