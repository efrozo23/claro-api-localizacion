package com.claro.dto.response;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class Data {
	@JsonProperty
	@ApiModelProperty(dataType = "String")
	private String ciudad;
	@JsonProperty
	@ApiModelProperty(dataType = "String")
	private String pais;
	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@ApiModelProperty(dataType = "String")
	private String fechaUbicacion;

	@JsonProperty
	@ApiModelProperty(dataType = "String")
	private String dpto;
	
	
	public Data() {
		
	}
	

	public Data(String ciudad, String pais, String fechaUbicacion, String dpto) {
		this.ciudad = ciudad;
		this.pais = pais;
		this.fechaUbicacion = fechaUbicacion;
		this.dpto = dpto;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getFechaUbicacion() {
		return fechaUbicacion;
	}

	public void setFechaUbicacion(String fechaUbicacion) {
		this.fechaUbicacion = fechaUbicacion;
	}

	public String getDpto() {
		return dpto;
	}

	public void setDpto(String dpto) {
		this.dpto = dpto;
	}

}
