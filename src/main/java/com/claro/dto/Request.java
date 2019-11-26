package com.claro.dto;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@JsonIgnoreProperties
@ApiModel(description = "Request DTO Object")
public class Request {

    @Valid
    private DataRequest data;

	public DataRequest getData() {
		return data;
	}

	public void setData(DataRequest data) {
		this.data = data;
	}
    
    
    
}
