package com.showmetables.service.json;

import java.io.Serializable;

public class BaseJSON implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5208425418028296417L;
	public boolean success = false;
	public String message = "";
	
	public BaseJSON(boolean success, String message){ 
		this.success = success;
		this.message = message;
	}
	public BaseJSON() {
		
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
