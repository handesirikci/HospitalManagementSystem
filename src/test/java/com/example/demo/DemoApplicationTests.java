package com.example.demo;

import com.example.CreateHospitalRequest;
import com.example.Hospital;
import com.example.HospitalManagementServiceGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
		"grpc.server.inProcessName=test", // Enable inProcess server
		"grpc.server.port=-1", // Disable external server
		"grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@SpringJUnitConfig(classes = {MyServiceUnitTestConfiguration.class})
@DirtiesContext
public class DemoApplicationTests {

	/*
	@GrpcClient("inProcess")
	private HospitalManagementServiceGrpc.HospitalManagementServiceBlockingStub myService;


	@Test
	@DirtiesContext
	public void testSayHello() {
		CreateHospitalRequest request = CreateHospitalRequest.newBuilder()
				.setName("LMU Klinikum")
				.build();
		Hospital response = myService.createHospital(request);
		assertNotNull(response);
		assertEquals("LMU Klinikum", response.getName());
	}

	 */

}
