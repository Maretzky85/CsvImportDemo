package com.sikoramarek.csvdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	@Column(name = "birth_date")
	LocalDate birthDate;

	@Column(length = 9)
	@Length(min = 9, max = 9)
	String phone_no;
}
