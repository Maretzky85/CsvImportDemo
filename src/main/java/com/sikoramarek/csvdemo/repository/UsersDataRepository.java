package com.sikoramarek.csvdemo.repository;

import com.sikoramarek.csvdemo.model.UsersData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersDataRepository extends JpaRepository<UsersData, Long> {
}
