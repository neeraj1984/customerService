package com.microservice.customerService.exception;

import org.springframework.http.ResponseEntity;


/*
 	2xx (Success)		indicates that the request was accepted successfully
	3xx (Redirection)	informs that the client must take further actions in order to complete the request
	4xx (Client error)	indicates an error in the request from the client side
	5xx (Server error)	tells that the server failed to fulfill the request
 */

public class ResponseEntityBuilder {

	public static ResponseEntity<Object> build(ApiError apiError) {
	      return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
