package com.sikoramarek.csvdemo.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.sikoramarek.csvdemo.controller.UploadController;
import com.sikoramarek.csvdemo.model.UsersData;
import com.sikoramarek.csvdemo.repository.UsersDataRepository;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadService {

	UsersDataRepository usersDataRepository;

	public UploadService(UsersDataRepository usersDataRepository){
		this.usersDataRepository = usersDataRepository;
	}

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	public ResponseEntity parseFile(MultipartFile multipartFile) {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
		MappingIterator<Map<String,String>> data = null;
		try {
			data = mapper.readerFor(Map.class)
					.with(schema)
					.readValues(multipartFile.getInputStream());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return parseData(data);
	}

	private ResponseEntity<Map<String, Integer>> parseData(MappingIterator<Map<String,String>> data) {
		Map<String, Integer> stats = new HashMap<>();
		stats.put("Total", 0);
		stats.put("Accepted", 0);
		stats.put("Failed", 0);
		while (data.hasNext()) {
			stats.put("Total", stats.get("Total")+1);
			Map<String, String> rowAsMap = data.next();
			if (parseIdentity(rowAsMap)) {
				stats.put("Accepted", stats.get("Accepted")+1);
			} else {
				stats.put("Failed", stats.get("Failed")+1);
			}
		}
		System.out.println(stats);
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}

	private boolean parseIdentity(Map<String, String> rowAsMap) {
		int size = rowAsMap.size();
		String date = checkDateFormat(rowAsMap.get("birth_date"));
		if(size < 3 || date == null) {
			logger.error("Error parsing Identity "+rowAsMap.toString());
			return false;
		}
		try {
			UsersData user = UsersData.builder()
					.first_name(rowAsMap.get("first_name"))
					.last_name(rowAsMap.get("last_name"))
					.birthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")))
					.build();
			if (size == 4){
				String phone_no = rowAsMap.get("phone_no");
				if (phone_no.length() != 9 ){
					logger.error("Phone number in wrong format");
					return false;
				}
				user.setPhone_no(phone_no);
			}
			usersDataRepository.save(user);
		} catch (DateTimeParseException e) {
			logger.error(e.toString());
			return false;
		}
		return true;
	}

	private String checkDateFormat(String birthday) {
		if (birthday != null && birthday.trim().length() > 6){
			String[] date = birthday.trim().split("\\.");
			StringBuilder sb = new StringBuilder();
			if (date.length == 3) {
				assert(date[0].length() == 4);
				sb.append(date[0]);
				if (date[1].length() == 1){
					date[1] = "0"+date[1];
				}
				sb.append(date[1]);
				if (date[1].length() == 1){
					date[2] = "0"+date[2];
				}
				sb.append(date[1]);
			}
			return sb.toString();
		} else {
			logger.error("error parsing birth date "+birthday);
		}
		return null;
	}

}
