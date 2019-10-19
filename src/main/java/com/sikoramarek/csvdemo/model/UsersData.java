package com.sikoramarek.csvdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users_data")
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

	@Column(length = 9, nullable = true)
	String phone_no;
}
