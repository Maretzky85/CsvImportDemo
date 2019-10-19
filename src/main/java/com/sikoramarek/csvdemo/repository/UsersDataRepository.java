package com.sikoramarek.csvdemo.repository;

import com.sikoramarek.csvdemo.model.UsersData;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UsersDataRepository extends JpaRepository<UsersData, Long> {

	Page<UsersData> findAll(Pageable pageable);

	@Query(nativeQuery = true,
			value ="SELECT * FROM USERS_DATA " +
			"WHERE BIRTH_DATE = (SELECT MIN(BIRTH_DATE) PHONE_NO FROM USERS_DATA WHERE PHONE_NO is not NULL)" +
			"AND" +
			"PHONE_NO is not NULL")
	UsersData findOldestUserWithPhone();
}
