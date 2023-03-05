package com.example.demo;

import com.example.HospitalManagementServiceImpl;
import com.example.repository.HospitalRepository;
import com.example.repository.PatientRepository;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class, // Create required server beans
        GrpcServerFactoryAutoConfiguration.class, // Select server implementation
        //GrpcClientAutoConfiguration.class
}) // Support @GrpcClient annotation
@Configuration
public class MyServiceUnitTestConfiguration {

    @Bean
    HospitalRepository getHospitalRepository() {
        return mock(HospitalRepository.class);
    }

    @Bean
    PatientRepository getPatientRepository() {
        return mock(PatientRepository.class);
    }
    @Bean
    HospitalManagementServiceImpl getHospitalManagementServiceImpl() {
        return new HospitalManagementServiceImpl(getHospitalRepository(), getPatientRepository());
    }

}
