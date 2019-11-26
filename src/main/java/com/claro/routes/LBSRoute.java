package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.beans.ValidatorDate;

@Component
public class LBSRoute extends RouteBuilder  {

	public static final String LBS_ROUTE ="direct://lbs-endpoint";
	
	public static final String LBS_COORDINATES ="direct://search-coor";
	
	public static final String LATITUD = "latitud";
	public static final String LONGITUD = "longitud";
	public static final String LATITUD_LBS = "latitudLbs";
	public static final String LONGITUD_LBS = "longitudLbs";
	public static final String FECHA_UBICACION = "fechaUbicacion";
	
	public static final String AUDIT_DETAILS = "auditDetails";
	public static final String HEADERS_ERROR = "error";
	
	private Logger logger = LoggerFactory.getLogger(LBSRoute.class);
	

	
	@Override
	public void configure() throws Exception {	
		
		
		from(LBS_ROUTE).routeId("LBS-SERVICE").streamCaching("true")
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Inicio a consumir el servicio de LBS ")
			.setProperty("user", simple("{{lbs.user}}"))
			.setProperty("password", simple("{{lbs.password}}"))
			.setProperty("prefijo",simple("{{lbs.prefijo}}"))

			.setHeader("CamelVelocityResourceUri").simple("{{lbs.template}}")
			.to("velocity:dummy?contentCache=true&propertiesFile=velocity.properties")
			.log(LoggingLevel.DEBUG, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Plantilla cargada || destalle: ${body}")
			.removeHeaders("*")
			.convertBodyTo(String.class)
			.log(LoggingLevel.DEBUG, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = {{lbs.url}}")
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setHeader(Exchange.HTTP_URI, simple("{{lbs.url}}"))
			.doTry()
				.to("http4://dummy?httpClient.connectTimeout={{lbs.connection.timeout}}&httpClient.socketTimeout={{lbs.connection.timeout}}&throwExceptionOnFailure=false")
			.endDoTry()
			.doCatch(Exception.class)
				.log(LoggingLevel.ERROR, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Error en el consumo del servicio LBS || Codigo: ${headers.CamelHttpResponseCode} ")
			.end()
			.choice()
				.when(simple("${headers.CamelHttpResponseCode} != 200 "))
					.log(LoggingLevel.ERROR, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Error en el consumo del servicio LBS || Codigo: ${headers.CamelHttpResponseCode} ")
					.setProperty(AUDIT_DETAILS, simple("SERVICIO NO DISPONIBLE || STATUS CODE: ${headers.CamelHttpResponseCode}"))
					.setHeader(HEADERS_ERROR, constant(400))
					.throwException(NullPointerException.class, "{{message.response.otros}}")
				.endChoice()
			.end()
			.log(LoggingLevel.DEBUG, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Servicio LBS OK || body : ${body}")
			.to(LBS_COORDINATES)
			.end();
		
		from(LBS_COORDINATES).routeId("SearchCoordinates").streamCaching()
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Inicio a buscar las coordenadas en la respuesta")
			.log(LoggingLevel.DEBUG, logger, "Exchange= ${exchangeProperty.procesoId} || body: ${body}")
			.setProperty(FECHA_UBICACION).xpath("/*/*/*/*/time/text()")
			.setProperty(LATITUD).xpath("string(//coord/X)",String.class)
			.setProperty(LONGITUD).xpath("string(//coord/Y)",String.class)
			.setProperty(LATITUD_LBS,simple("${exchangeProperty.latitud}"))
			.setProperty(LONGITUD_LBS,simple("${exchangeProperty.longitud}"))
			.log(LoggingLevel.DEBUG, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Datos obtenidos | fecha: ${exchangeProperty.fechaUbicacion} | latitud:${exchangeProperty.latitud} | longitud:${exchangeProperty.longitud}")
			.choice()
				.when(simple(" ${exchangeProperty.fechaUbicacion} == null || ${exchangeProperty.fechaUbicacion} == '' || ${exchangeProperty.latitud} == '' || ${exchangeProperty.longitud} == '' "))
					.log(LoggingLevel.ERROR, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Falta uno de los siguientes valores | fecha: ${exchangeProperty.fechaUbicacion} | latitud:${exchangeProperty.latitud} | longitud:${exchangeProperty.longitud}")
					.setProperty(AUDIT_DETAILS, simple("Falta uno de los siguientes valores | fecha: ${exchangeProperty.fechaUbicacion} | latitud:${exchangeProperty.latitud} | longitud:${exchangeProperty.longitud}"))
					.throwException(Exception.class, "{{message.response.error}}")
					
				.endChoice()
				.otherwise()
					.log(LoggingLevel.TRACE, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Validando diferencia de minutos : {{lbs.tiempomaximo}} | Fecha: ${exchangeProperty.fechaUbicacion}")
					.bean(ValidatorDate.class)
				.endChoice()
			.end()
			.to(TransitionRoute.ROUTE_UBICACITION)
			.end();
		
	}
	
	
}