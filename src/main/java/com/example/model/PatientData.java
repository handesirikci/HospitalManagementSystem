package com.example.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@Data
public class PatientData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    int nationalIdNumber;

    @ManyToMany(mappedBy= "registeredPatients" ,fetch = FetchType.EAGER)
    @JsonManagedReference
    List<HospitalData> registeredHospitals;
}
