package com.claro.configuration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.claro.dto.Request;
import com.claro.dto.Response;
import com.claro.dto.response.Audit;
import com.claro.routes.LBSRoute;
import com.claro.routes.RestDslMainRoute;

@Component
@EnableJms
public class JmsConfig {

	Logger logger = LoggerFactory.getLogger(JmsConfig.class);

	@Autowired
	public JmsTemplate jmsTemplate;

	@Autowired
	Environment env;

	private static final String DATE_ZONE = "America/Bogota";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String API_NAME = "api_name";
	
	public Audit generateMenssage(Exchange exchange) {
		Request request = exchange.getProperty(RestDslMainRoute.REQUEST, Request.class);
		Response response = exchange.getProperty(RestDslMainRoute.RESPONSE, Response.class);

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DATE_ZONE));
		request.getData().setCliente(exchange.getProperty(RestDslMainRoute.NOMBRE_CLIENTE, String.class)==null?" ":exchange.getProperty(RestDslMainRoute.NOMBRE_CLIENTE, String.class));
		Audit audit = new Audit();
		audit.setApi(env.getProperty("api.name"));
		audit.setFechaTransaccion(DateTimeFormatter.ofPattern(DATE_FORMAT).format(now));
		audit.setRequest(request);
		audit.setResponse(response);
		audit.setRecursoApp(env.getProperty("endpoint.api"));
		audit.setLogUUIDTxr(exchange.getProperty("procesoId", String.class));
		audit.setLogUsuario(env.getProperty("api.log.name"));
		audit.setDetalleR(response.getRespuesta().getMensajeRespuesta());
		audit.setIdCliente(exchange.getProperty(RestDslMainRoute.ID_CLIENTE, String.class)==null?" ":exchange.getProperty(RestDslMainRoute.ID_CLIENTE, String.class));
		audit.setLatitudLbs(exchange.getProperty(LBSRoute.LATITUD_LBS, String.class));
		audit.setLongitudLbs(exchange.getProperty(LBSRoute.LONGITUD_LBS, String.class));
		audit.setRequestLBS(exchange.getProperty("SOLICITUD", String.class));
		audit.setResponseLBS(exchange.getProperty("RESPUESTA", String.class));
		audit.setStatusLBS(exchange.getProperty("STATUS", String.class));
		audit.setResponsePIC(exchange.getProperty("body", String.class));
		audit.setStatusPIC(exchange.getProperty("ESTADOPIC", String.class));
		return audit;
	}

	@Handler
	public void sendMessage(String payload) {
		this.jmsTemplate.convertAndSend(env.getProperty("spring.activemq.queue"), payload, m ->{
			m.setStringProperty(API_NAME, env.getProperty("api.name"));
			return m;
		});

	}
}
