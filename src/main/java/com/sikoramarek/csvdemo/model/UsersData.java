package com.sikoramarek.csvdemo.model;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
public class UsersData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Length(max = 50)
	String first_name;

	@NotNull
	@Length(max = 50)
	String last_name;

	@NotNull
	LocalDate birth_date;

	@Column(length = 9)
	@Length(min = 9, max = 9)
	String phone_no;
}
