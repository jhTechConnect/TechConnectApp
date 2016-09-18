package org.centum.techconnect.model;

public class LoginAttempt {
	private String email;
	private String password;
	
	public LoginAttempt(String em, String pass) {
		this.email = em;
		this.password = pass;
	}
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String em) {
		this.email = em;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String p) {
		this.password = p;
	}
}
