package com.showmetables.service.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7534558030258617590L;
	private String column;
	private String operator;
	private String value;
	private ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
	private String conditionType;
	
	public Condition() {
		
	}
	public Condition(String column, String operator, String value) {
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<Condition> getAdditionalConditions() {
		return additionalConditions;
	}

	public void setAdditionalConditions(ArrayList<Condition> additionalConditions) {
		this.additionalConditions = additionalConditions;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	public boolean hasMoreConditions(){
		for (Condition c : this.additionalConditions){
			if (c.getAdditionalConditions().size() > 0) {
				return true;
			}
		}
		return false;
	}
}
