package com.microservice.customerService.integration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerIntegrationTest {
	
	final Logger logger = LoggerFactory.getLogger(CustomerControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;
    
    private MockRestServiceServer mockRestServer;
    
    
    @BeforeEach
    void setUp() {
    	mockRestServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    
    @Test
    void testGetAddress() throws Exception {
    	
    	logger.info("testGetAddress() starts.....");
    	
        String externalCallResult = "address2"; // Populate with test data
        String expectedMockResponse = "address Details address2";
        
        // Create a strongly-typed ParameterizedTypeReference
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<String>() {};
    	    	        
        ResponseEntity<String> responseEntity = ResponseEntity.ok(externalCallResult);
        when(restTemplate.exchange(
                eq("http://address-service/addressAPI/address2"),
                eq(HttpMethod.GET),
                eq(null),
                eq(responseType) // Match the ParameterizedTypeReference as used in controller
        )).thenReturn(responseEntity);
        
        logger.info("testGetAddress() response:: "+responseEntity.getBody());

        // Perform the request and assert the response
        mockMvc.perform(get("/customersAPI/getCustomerAddress")) // Replace with your actual endpoint
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMockResponse)); // Adjust based on your expected response
        
        logger.info("testGetAddress() ends.....");
    }
    
}
