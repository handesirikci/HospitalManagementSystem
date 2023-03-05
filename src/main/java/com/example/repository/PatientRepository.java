package com.example.repository;


import com.example.model.PatientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientData, Long> {

    Optional<PatientData> findByNationalIdNumber(int nationalIdNumber);
}
