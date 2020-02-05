package com.orange.lo.sample.kerlinl2lo.kerlink.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndDeviceDto {

	@JsonProperty("devEui")
	private String devEui = null;
	
	@JsonProperty("name")
	private String name = null;
	
	@JsonProperty("devAddr")
	private String devAddr = null;

	@JsonProperty("country")
	private String country = null;

	@JsonProperty("status")
	private String status = null;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDevAddr() {
		return devAddr;
	}

	public void setDevAddr(String devAddr) {
		this.devAddr = devAddr;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDevEui() {
		return devEui;
	}

	public void setDevEui(String devEui) {
		this.devEui = devEui;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "EndDeviceDto [devEui=" + devEui + ", name=" + name + ", devAddr=" + devAddr + ", country=" + country
				+ ", status=" + status + "]";
	}
}