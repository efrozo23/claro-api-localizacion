package com.claro.beans;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.claro.dto.Response;
import com.claro.dto.response.Data;
import com.claro.dto.response.Respuesta;
import com.claro.routes.LBSRoute;
import com.claro.routes.TransitionRoute;

import io.swagger.annotations.ApiModelProperty;

@Component
public class ResponseHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;

	@Handler
	@ApiModelProperty(notes = "Parametro De Salida")
	public Response handler() {

		return new Response();
	}

	public Response buildResponseError(String codigo, String mensaje, String excepcion){
		Response dto = new Response();
		Data data = new Data("", "", "", "");
		Respuesta respuesta = new Respuesta();
		respuesta.setCodigoRespuesta(codigo);

		if (excepcion != null) {

			respuesta.setMensajeRespuesta(env.getProperty(mensaje) + " -" + env.getProperty(excepcion));

		} else {
			respuesta.setMensajeRespuesta(env.getProperty(mensaje));
		}
		logger.info("Mensaje Original:{} ", mensaje);
		
		
		String defaultcharset = Charset.defaultCharset().displayName();
		logger.info("Codificación usada:{}", defaultcharset);
		respuesta.setMensajeRespuesta(new String(respuesta.getMensajeRespuesta().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8));
		logger.info("Mensaje codificado:{} ", respuesta.getMensajeRespuesta());
		dto.setData(data);
		dto.setRespuesta(respuesta);
		return dto;
	}

	public Response buildResponse(Exchange exchange) {

		String codigoRespuesta = (String) exchange.getProperty("codigoRespuesta");
		String mensajeRespuesta = (String) exchange.getProperty("mensajeRespuesta");
		@SuppressWarnings("unchecked")
		Map<String, Object> data = exchange.getProperty(TransitionRoute.RESPONSE_SP, Map.class);

		Response dto = new Response();

		Respuesta respuesta = new Respuesta();
		Data dataResponse = new Data();
		dataResponse.setPais(data.get("pais_out").toString());
		dataResponse.setDpto(data.get("dpto_out").toString());
		dataResponse.setCiudad(data.get("ciudad_out").toString());
		dataResponse.setFechaUbicacion(exchange.getProperty(LBSRoute.FECHA_UBICACION, String.class));
		respuesta.setCodigoRespuesta(codigoRespuesta);
		respuesta.setMensajeRespuesta(mensajeRespuesta);
		dto.setData(dataResponse);
		dto.setRespuesta(respuesta);
		return dto;
	}
}
