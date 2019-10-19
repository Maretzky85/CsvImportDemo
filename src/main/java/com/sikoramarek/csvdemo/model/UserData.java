package com.sikoramarek.csvdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "Users_data")
public class UserData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//			For sending json without ID
//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	Long id;

	@NotNull
	@Length(max = 50)
	@Column(name = "first_name")
	String firstName;

	@NotNull
	@Length(max = 50)
	@Column(name = "last_name")
	String lastName;

	@NotNull
	@Column(name = "birth_date")
	LocalDate birthDate;

	@Column(name = "phone_no", length = 9, unique = true)
	String phoneNo;

}
