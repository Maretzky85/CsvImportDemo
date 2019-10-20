package com.sikoramarek.csvdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import net.bytebuddy.build.ToStringPlugin;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormatter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

@Entity
@Getter
@Setter
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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	LocalDate birthDate;

	public int getAge() {
		LocalDate birthDate = getBirthDate();
		LocalDate now = LocalDate.now();
		return Period.between(birthDate, now).getYears();
	}

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	int age;

	@Column(name = "phone_no", length = 9, unique = true)
	String phoneNo;

}
