package com.sikoramarek.csvdemo.repository;

import com.sikoramarek.csvdemo.model.UsersData;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface UsersDataRepository extends JpaRepository<UsersData, Long> {

	Page<UsersData> findAll(Pageable pageable);
}
