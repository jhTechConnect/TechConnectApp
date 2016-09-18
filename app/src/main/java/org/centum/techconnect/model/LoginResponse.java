package org.centum.techconnect.model;


public class LoginResponse {

	private String status;
	private Tokens data;
	private String message;

	/**
	* 
	* @return
	* The status
	*/
	public String getStatus() {
	return status;
	}
	
	/**
	* 
	* @param status
	* The status
	*/
	public void setStatus(String status) {
	this.status = status;
	}
	
	/**
	* 
	* @return
	* The data
	*/
	public Tokens getData() {
	return data;
	}
	
	/**
	* 
	* @param data
	* The data
	*/
	public void setData(Tokens data) {
	this.data = data;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String m) {
		this.message = m;
	}

}