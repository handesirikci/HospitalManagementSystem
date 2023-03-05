package com.example.repository;


import com.example.model.HospitalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<HospitalData, Long> {

    Optional<HospitalData> findByName(String name);
    Optional<HospitalData> findById(Long id);
}
