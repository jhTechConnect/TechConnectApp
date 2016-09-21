package com.java.model;

import com.google.gson.JsonObject;

public class JsendResponse {

	private String status;
	private JsonObject data; //This allows us to look for specific keys given the particular call
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
	public JsonObject getData() {
	return data;
	}
	
	/**
	* 
	* @param data
	* The data
	*/
	public void setData(JsonObject data) {
	this.data = data;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String m) {
		this.message = m;
	}

}