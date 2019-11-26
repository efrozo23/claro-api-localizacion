package com.claro.dto.response;

import java.io.Serializable;


import javax.xml.bind.annotation.XmlRootElement;

import com.claro.dto.Request;
import com.claro.dto.Response;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Assert solutions
 *
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class Audit implements Serializable {

	private static final long serialVersionUID = 5781893527765898031L;

	@JsonProperty
	@ApiModelProperty(dataType = "String")
	private String api;
	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "America/Bogota")
	private String fechaTransaccion;
	
	@JsonProperty
	private String logUUIDTxr;
	
	@JsonProperty 
	private String logUsuario;
	
	@JsonProperty
	private String recursoApp;
	
	@JsonProperty
	private String detalleR;
	
	@JsonProperty
	private String idCliente;
	
	@JsonProperty
	private String latitudLbs;
	
	@JsonProperty
	private String longitudLbs;

	private Request request;
	
	private Response response;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public String getLogUUIDTxr() {
		return logUUIDTxr;
	}

	public void setLogUUIDTxr(String logUUIDTxr) {
		this.logUUIDTxr = logUUIDTxr;
	}

	public String getLogUsuario() {
		return logUsuario;
	}

	public void setLogUsuario(String logUsuario) {
		this.logUsuario = logUsuario;
	}

	public String getRecursoApp() {
		return recursoApp;
	}

	public void setRecursoApp(String recursoApp) {
		this.recursoApp = recursoApp;
	}

	public String getDetalleR() {
		return detalleR;
	}

	public void setDetalleR(String detalleR) {
		this.detalleR = detalleR;
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	public String getLatitudLbs() {
		return latitudLbs;
	}

	public void setLatitudLbs(String latitudLbs) {
		this.latitudLbs = latitudLbs;
	}

	public String getLongitudLbs() {
		return longitudLbs;
	}

	public void setLongitudLbs(String longitudLbs) {
		this.longitudLbs = longitudLbs;
	}
	
	

}
