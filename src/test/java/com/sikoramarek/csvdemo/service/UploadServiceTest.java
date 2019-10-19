package com.sikoramarek.csvdemo.service;

import com.sikoramarek.csvdemo.CsvDemoApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CsvDemoApplication.class)
public class UploadServiceTest {

	protected MockMvc mvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Autowired
	UploadService uploadService;

	String testString = "first_name;last_name;birth_date;phone_no\n" +
			"Stefan;Testowy;1988.11.11;600700800\n" +
			"Maria;Ziółko;1999.1.1;555666777;\n" +
			";;;\n" +
			"Jolanta;Magia;2000.02.04;666000111\n" +
			"\n" +
			" marian;kowalewski;1950.10.01;670540120\n" +
			"Zygmunt;\tStefanowicz;1991.06.01;-\n" +
			"Ernest;Gąbka;1991.06.01;\n" +
			"Elżbieta;Żółw;1988.03.03;670540120";

	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void parseFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("test.csv"));
		writer.write(testString);
		writer.close();
		InputStream multipartFile = new FileInputStream(new File("test.csv"));
		ResponseEntity result = uploadService.parseFile(new MockMultipartFile("test.csv", multipartFile));
		System.out.println(result);
	}
}