package com.microservice.customerService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MicroServiceTestController {
	
	@RequestMapping(value = "/getCustomer", method = RequestMethod.GET)
	public String getCustomer() {
		System.out.println("calling getCustomer()");		
		return "Hello Customer" ;
	}

}
