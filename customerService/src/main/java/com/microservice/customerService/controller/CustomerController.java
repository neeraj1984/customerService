package com.microservice.customerService.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

	// @GetMapping
	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping("/all")
	public List<Customer> customerList() {
		logger.info("customerList() started....");
		Customer c1 = new Customer("1", "Andy", "Capgemini", LocalDate.now().minusYears(28));
		Customer c2 = new Customer("2", "Mark", "LiquidHub", LocalDate.now().minusYears(24));

		List<Customer> result = new ArrayList<>();
		result.add(c1);
		result.add(c2);
		return result;
	}

	@RequestMapping(value = "/getCustomerAddress", method = RequestMethod.GET)
	public String getAddress() {
		System.out.println("calling getAddress()");
		//RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address2", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		System.out.println("Response Received as " + response);

		return "address Details " + response;
	}

	/*
	 * resilience4j Retry implementation
	 */
	@RequestMapping(value = "/getCustomerAddress2", method = RequestMethod.GET)
	@Retry(name = "addressRetry", fallbackMethod = "getAddress2Fallback")
	public String getAddress2() {
		System.out.println("calling getAddress2()");
		// RestTemplate restTemplate = new RestTemplate();//with this service URL does
		// not work
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address2", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		System.out.println("Response Received as " + response);

		return "address Details " + response;
	}
	
	/*
	 * resilience4j Circuit breaker implementation
	 */
	@RequestMapping(value = "/getCustomerAddress3", method = RequestMethod.GET)
	@CircuitBreaker(name = "addressRetryCB", fallbackMethod = "getAddress2Fallback")
	public String getAddress3() {
		System.out.println("calling getAddress3()");
		// RestTemplate restTemplate = new RestTemplate();//with this service URL does
		// not work
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address2", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		System.out.println("Response Received as " + response);

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
	
	@RequestMapping(value = "/getCustomerAddress4", method = RequestMethod.GET)
	public String getAddressWithFeign() {
		return addressServiceConsumer.getAddress();
	}
	
	@RequestMapping(value = "/getCustomerAddressDB", method = RequestMethod.GET)
	public String getAddressDB() {
		System.out.println("calling getAddressDB()");
		String response = restTemplate.exchange(address_service_uri + "/addressAPI/address1", HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				}).getBody();

		System.out.println("Response Received as " + response);

		return "address Details " + response;
	}

}
