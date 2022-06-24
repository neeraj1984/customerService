package com.microservice.customerService.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="address-service")
public interface AddressServiceConsumer {
	
	@GetMapping("/addressAPI/address1")
    public String getAddress();

}
