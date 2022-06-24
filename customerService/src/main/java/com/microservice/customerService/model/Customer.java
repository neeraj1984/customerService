package com.microservice.customerService.model;

import java.time.LocalDate;


public class Customer {
	
	private String id;
    private String name;
    private String company;
    private LocalDate birthdate;
	
    
    public Customer(String id,String name,String company,LocalDate birthdate) {
    	this.id = id;
    	this.name = name;
    	this.company = company;
    	this.birthdate = birthdate;
    	
    }


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}


	public LocalDate getBirthdate() {
		return birthdate;
	}


	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}
    
}
