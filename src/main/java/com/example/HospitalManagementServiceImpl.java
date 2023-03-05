package com.example;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import com.example.model.HospitalData;
import com.example.model.PatientData;
import com.example.repository.HospitalRepository;
import com.example.repository.PatientRepository;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@GrpcService
public class HospitalManagementServiceImpl extends HospitalManagementServiceGrpc.HospitalManagementServiceImplBase {

    final HospitalRepository hospitalRepository;

    final PatientRepository patientRepository;
    @Override
    public void createHospital(CreateHospitalRequest request, StreamObserver<Hospital> responseStreamObserver) {
        HospitalData hospitalData = new HospitalData();
        hospitalData.setName(request.getName());
        hospitalRepository.save(hospitalData);
        Hospital response = Hospital.newBuilder()
                .setName(request.getName())
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }
    @Override
    public void createPatient(CreatePatientRequest request, StreamObserver<Patient> responseStreamObserver) {
        PatientData patientData = new PatientData();
        patientData.setNationalIdNumber(request.getNationalIdNumber());
        patientRepository.save(patientData);

        Patient response = Patient.newBuilder()
                .setNationalIdNumber(request.getNationalIdNumber())
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deleteHospital(DeleteHospitalRequest request, StreamObserver<DeleteResponse> responseStreamObserver) {
        Optional<HospitalData> hospitalDataMaybe = hospitalRepository.findByName(request.getName());
        DeleteResponse deleteResponse;
        if(!hospitalDataMaybe.isEmpty()) {
            for(PatientData patientData: hospitalDataMaybe.get().getRegisteredPatients()) {
                patientData.getRegisteredHospitals().remove(hospitalDataMaybe.get());
                patientRepository.save(patientData);
            }
            hospitalRepository.deleteById(hospitalDataMaybe.get().getId());
            deleteResponse = DeleteResponse.newBuilder()
                    .setIsDeleted(true)
                    .build();

        } else {
            deleteResponse = DeleteResponse.newBuilder()
                    .setIsDeleted(false)
                    .build();
        }
        responseStreamObserver.onNext(deleteResponse);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deletePatient(DeletePatientRequest request, StreamObserver<DeleteResponse> responseStreamObserver) {
        Optional<PatientData> patientDataMaybe = patientRepository.findByNationalIdNumber((request.getNationalIdNumber()));
        DeleteResponse deleteResponse;
        if(!patientDataMaybe.isEmpty()) {
            for(HospitalData hospitalData: patientDataMaybe.get().getRegisteredHospitals()) {
                hospitalData.getRegisteredPatients().remove(patientDataMaybe.get());
                hospitalRepository.save(hospitalData);
            }
            patientRepository.deleteById(patientDataMaybe.get().getId());
            deleteResponse = DeleteResponse.newBuilder()
                    .setIsDeleted(true)
                    .build();
        } else {
            deleteResponse = DeleteResponse.newBuilder()
                    .setIsDeleted(false)
                    .build();
        }
        responseStreamObserver.onNext(deleteResponse);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void updateHospital(Hospital request, StreamObserver<Hospital> responseStreamObserver) {
        Optional<HospitalData> hospitalDataMaybe = hospitalRepository.findById(request.getId());
        if(!hospitalDataMaybe.isEmpty()) {
            HospitalData hospitalToUpdate = hospitalDataMaybe.get();
            hospitalToUpdate.setName(request.getName());
            hospitalToUpdate.setRegisteredPatients(getPatientDataList(request.getRegisteredPatientsList()));
            hospitalRepository.save(hospitalToUpdate);
            Hospital responseHospital = Hospital.newBuilder()
                    .addAllRegisteredPatients(request.getRegisteredPatientsList())
                    .setName(request.getName())
                    .build();

            responseStreamObserver.onNext(responseHospital);
            responseStreamObserver.onCompleted();

        } else {
            responseStreamObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void updatePatient(Patient request, StreamObserver<Patient> responseStreamObserver) {
        Optional<PatientData> patientDataMaybe = patientRepository.findById(request.getId());
        if(!patientDataMaybe.isEmpty()) {
            PatientData patientToUpdate = patientDataMaybe.get();
            patientToUpdate.setNationalIdNumber(request.getNationalIdNumber());
            patientToUpdate.setRegisteredHospitals(getHospitalDataList(request.getRegisteredHospitalsList()));
            patientRepository.save(patientToUpdate);
            Patient responsePatient = Patient.newBuilder()
                    .addAllRegisteredHospitals(request.getRegisteredHospitalsList())
                    .setNationalIdNumber(request.getNationalIdNumber())
                    .build();

            responseStreamObserver.onNext(responsePatient);
            responseStreamObserver.onCompleted();

        } else {
            responseStreamObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void registerPatientToHospital(RegisterRequest registerRequest, StreamObserver<RegisterResponse> responseStreamObserver) {
        Optional<PatientData> patientDataMaybe = patientRepository.findByNationalIdNumber(registerRequest.getPatientNationalIdNumber());
        Optional<HospitalData> hospitalDataMaybe = hospitalRepository.findByName(registerRequest.getHospitalName());
        RegisterResponse registerResponse;
        if(!patientDataMaybe.isEmpty() && !hospitalDataMaybe.isEmpty()) {
            patientDataMaybe.get().getRegisteredHospitals().add(hospitalDataMaybe.get());
            hospitalDataMaybe.get().getRegisteredPatients().add(patientDataMaybe.get());
            patientRepository.save(patientDataMaybe.get());
            hospitalRepository.save(hospitalDataMaybe.get());
            registerResponse = RegisterResponse.newBuilder()
                    .setIsRegistered(true)
                    .build();
        } else {
            registerResponse = RegisterResponse.newBuilder()
                    .setIsRegistered(false)
                    .build();
        }
        responseStreamObserver.onNext(registerResponse);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void listRegisteredHospitals(PatientListRequest request, StreamObserver<HospitalListResponse> responseStreamObserver) {
        Optional<PatientData> patientDataMaybe = patientRepository.findByNationalIdNumber(request.getPatientNationalIdNumber());
        if(!patientDataMaybe.isEmpty()) {
            List<String> hospitalList = patientDataMaybe.get().getRegisteredHospitals().stream()
                    .map(HospitalData::getName)
                    .collect(Collectors.toList());
            HospitalListResponse hospitalListResponse = HospitalListResponse.newBuilder()
                    .addAllRegisteredHospitalNames(hospitalList)
                    .build();

            responseStreamObserver.onNext(hospitalListResponse);
            responseStreamObserver.onCompleted();
        } else {
            responseStreamObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void listRegisteredPatients(HospitalListRequest request, StreamObserver<PatientListResponse> responseStreamObserver) {
        Optional<HospitalData> hospitalDataMaybe = hospitalRepository.findByName(request.getHospitalName());
        if(!hospitalDataMaybe.isEmpty()) {
            List<Integer> patientsNationalIds = hospitalDataMaybe.get().getRegisteredPatients().stream()
                    .map(PatientData::getNationalIdNumber)
                    .collect(Collectors.toList());
            PatientListResponse patientList = PatientListResponse.newBuilder()
                    .addAllRegisteredPatientsNationalIds(patientsNationalIds)
                    .build();

            responseStreamObserver.onNext(patientList);
            responseStreamObserver.onCompleted();
        } else {
            responseStreamObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    private List<PatientData> getPatientDataList(List<Patient> patientList) {
        List<PatientData> result = new ArrayList<>();
        for(Patient patient : patientList) {
            PatientData patientData = new PatientData();
            patientData.setId(patient.getId());
            patientData.setNationalIdNumber(patient.getNationalIdNumber());
            result.add(patientData);
        }
        return result;
    }

    private List<HospitalData> getHospitalDataList(List<Hospital> hospitalList) {
        List<HospitalData> result = new ArrayList<>();
        for(Hospital hospital : hospitalList) {
            HospitalData hospitalData = new HospitalData();
            hospitalData.setId(hospital.getId());
            hospitalData.setName(hospital.getName());
            result.add(hospitalData);
        }
        return result;
    }


}
