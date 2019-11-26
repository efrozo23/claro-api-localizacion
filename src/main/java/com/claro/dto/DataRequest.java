package com.claro.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class DataRequest {
    @JsonProperty
    @ApiModelProperty(dataType = "Integer")
    @Size(min = 10, max = 10, message = "celular.size")    
    @Pattern(regexp = "^[0-9]*$", message = "celular.caracteres.especiales")
    private String numeroCelular;
    @JsonProperty
    @ApiModelProperty(dataType = "String")
    private String cliente;

    public DataRequest() {
    	
    }
    
    public DataRequest(String numeroCelular) {
    	this.numeroCelular= numeroCelular;
    }
    
    
    

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

}
