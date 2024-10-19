package com.microservice.customerService.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservice.customerService.exception.ResourceNotFoundException;
import com.microservice.customerService.model.Customer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;


@RestController
@RequestMapping("/customersAPI")
public class CustomerController {

	@Autowired
	RestTemplate restTemplate;
	
	/* alternative to restTemplate */
	@Autowired
	AddressServiceConsumer addressServiceConsumer;

	final static String address_service_uri = "http://address-service";

	final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	//below properties will render from application.yml as Integer and boolean(not String). We can have both .properties and .yml in the project.
	//.yml takes precedence over .properties for common properties
	@Value("${app.version}")
	private Integer version;

	@Value("${app.isMiroserivce}")
	private boolean isMicroService;
	
	@Value("${microservice.app.list}")
	private List<String> applicationList;

	@GetMapping("/")
	public String customer() {
		logger.info("customer() started....");
		String retval = "service name is: "+applicationName + ", version: "+version + ", is Miscroservice? "+isMicroService + ", apps: "+applicationList;
		return "Hello Customer \n"+ retval;
	}

	//@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(value  = "/customerList" , produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> customerList() {
		logger.info("customerList() started....");
		List<String> result = new ArrayList<>();
		ObjectMapper om = new ObjectMapper();
		 // support Java 8 date time apis
        om.registerModule(new JavaTimeModule());

		try {
			Customer c1 = new Customer("1", "Andy", "Capgemini", LocalDate.of(1990, 1, 15));
			Customer c2 = new Customer("2", "Mark", "LiquidHub", LocalDate.of(1985, 5, 25));
			
			//String result1 = om.writerWithDefaultPrettyPrinter().writeValueAsString(c1);
			//String result2 = om.writerWithDefaultPrettyPrinter().writeValueAsString(c2);
			
			String result1 = om.writeValueAsString(c1);
			String result2 = om.writeValueAsString(c2);
			
			result.add(result1);
			result.add(result2);
			
		} catch (ResourceNotFoundException e) {
			logger.error("Exception in getting customer....");
			throw new ResourceNotFoundException("Customer not found!");

		} catch (JsonProcessingException e) {
			logger.error("Parsing error while getting customer....");
			e.printStackTrace();
		}catch(Exception  e) {
			logger.error("*****error in processing the data*****");
			e.printStackTrace();
		}
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
	}
	
	@GetMapping(value  = "/customerNames")
	public ResponseEntity<List<String>> customerList2() {
		logger.info("customerList2() started....");
		List<String> result = new ArrayList<>();
		//HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
		try {						
			result.add("Neeraj");
			result.add("Joshi");
		}catch(Exception  e) {
			logger.error("*****error in processing the data*****");
			e.printStackTrace();
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping(value = "/getCustomerAddress")
	public String getAddress() {
		logger.info("calling getAddress()");
		//RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address2", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		logger.info("Response Received as " + response);

		return "address Details " + response;
	}

	/*
	 * resilience4j Retry implementation
	 */
	@RequestMapping(value = "/getCustomerAddressWithRetry", method = RequestMethod.GET)
	@Retry(name = "addressRetry", fallbackMethod = "getAddress2Fallback")
	public String getAddress2() {
		logger.info("calling getAddress2()");
		// RestTemplate restTemplate = new RestTemplate();//with this service URL does
		// not work
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address2", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		logger.info("Response Received as " + response);

		return "address Details " + response;
	}
	
	/*
	 * resilience4j Circuit breaker implementation
	 */
	@RequestMapping(value = "/getCustomerAddressWithCB", method = RequestMethod.GET)
	@CircuitBreaker(name = "addressRetryCB", fallbackMethod = "getAddress2Fallback")
	public String getAddress3() {
		logger.info("calling getAddress3()");
		// RestTemplate restTemplate = new RestTemplate();//with this service URL does
		// not work
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address2", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		logger.info("Response Received as " + response);

		return "address Details " + response;
	}

	/*
	 * Fallback method for /getCustomerAddress2
	 * It should have the same params as the actual method, here we are not using any param
	 */
	public String getAddress2Fallback(Exception e) {
		logger.info("---RESPONSE FROM FALLBACK METHOD---");
		return "SERVICE IS DOWN, PLEASE TRY AFTER SOMETIME !!!";
	}
	
	@GetMapping(value = "/getCustomerAddress4")
	public String getAddressWithFeign() {
		return addressServiceConsumer.getAddress();
	}
	
	@RequestMapping(value = "/getCustomerAddressDB", method = RequestMethod.GET)
	public String getAddressDB() {
		logger.info("calling getAddressDB()");
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address1", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		logger.info("Response Received as " + response);

		return "address Details " + response;
	}
	
	/*
	 * Accessing Address API using WebClient
	 */
	@GetMapping(value = "/getCustomerAddressDBWebClient")
	public String getAddresswithWebClient() {
		WebClient client1 = WebClient.create();
		WebClient client2 = WebClient.create("http://localhost:8080");
		/*
		WebClient client3 = WebClient.builder()
				  .baseUrl("http://localhost:8080")
				  .defaultCookie("cookieKey", "cookieValue")
				  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
				  .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
				  .build();
		*/
		return "accessed the application using WebClient";
	}

}
