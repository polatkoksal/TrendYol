package com.trendyol.model;

import java.io.Serializable;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -5998945502131077584L;

	private Integer id;
	private String name;
	private String type;
	private String value;
	private Boolean isActive;
	private String ApplicationName;

	public Integer getID() {
		return id;
	}

	public void setID(Integer id) {
		id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getApplicationName() {
		return ApplicationName;
	}

	public void setApplicationName(String applicationName) {
		ApplicationName = applicationName;
	}

}
