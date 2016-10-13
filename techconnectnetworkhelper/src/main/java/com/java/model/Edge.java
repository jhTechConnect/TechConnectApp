package com.java.model;

public class Edge {
	//Fields defined as Edge by our GraphSON
	private String _id;
	private String _label;
	private String _outV; //source vertex
	private String _inV; //target vertex
	private String details;
	
	public String getId() {
		return this._id;
	}
	
	public void setId(String id) {
		this._id = id;
	}
	
	public String getLabel() {
		return this._label;
	}
	
	public void setLabel(String label) {
		this._label = label;
	}
	
	public String getOutV() {
		return this._outV;
	}
	
	public void setOutV(String outV) {
		this._outV = outV;
	}
	
	public String getInV() {
		return this._inV;
	}
	
	public void setInV(String inV) {
		this._inV = inV;
	}
	
	public String getDetails() {
		return this.details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	
	

}
