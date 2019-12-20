package com.claro.dto;

public class roamingLocation {
	private String timestamp;
	private String msisdn;
	private String vlrgt;
	private String country;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getVlrgt() {
		return vlrgt;
	}

	public void setVlrgt(String vlrgt) {
		this.vlrgt = vlrgt;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "PICResponse [timestamp=" + timestamp + ", msisdn=" + msisdn + ", vlrgt=" + vlrgt + ", country="
				+ country + "]";
	}
}