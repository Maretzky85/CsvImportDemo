package com.sikoramarek.csvdemo.repository;

import com.sikoramarek.csvdemo.model.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Long> {

	Page<UserData> findAll(Pageable pageable);

	@Query(nativeQuery = true,
			value = "SELECT * FROM USERS_DATA " +
					"WHERE BIRTH_DATE = " +
					"(SELECT MIN(BIRTH_DATE) PHONE_NO FROM USERS_DATA WHERE PHONE_NO is not NULL) " +
					"AND PHONE_NO is not NULL")
	UserData findOldestUserWithPhone();

	UserData findByPhoneNoEquals(String phone_no);

	Optional<List<UserData>> findAllByLastNameLike(String lastname);
}
