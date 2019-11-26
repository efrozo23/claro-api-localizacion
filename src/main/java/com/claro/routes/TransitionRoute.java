package com.claro.routes;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Map;



import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.beans.ValidatorDate;

@Component
public class TransitionRoute extends RouteBuilder {


	private Logger logger = LoggerFactory.getLogger(TransitionRoute.class);

	
	
	public static final String ROUTE_UBICACITION = "direct://search-ubication";
	
	public static final String RESPONSE_SP = "ubicacion"; 

    @Override
    public void configure() throws Exception {
        
        // @formatter:off
        from(ROUTE_UBICACITION).routeId("SearchUbication").streamCaching()
        .errorHandler(noErrorHandler())
        .log(LoggingLevel.INFO, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Inicio a consultar la ubicacion Nacional ")
        .setProperty(LBSRoute.LATITUD).method(ValidatorDate.class, "convertDecimal(${exchangeProperty.latitud})")
        .setProperty(LBSRoute.LONGITUD).method(ValidatorDate.class, "convertDecimal(${exchangeProperty.longitud})")
        .setHeader("lat", exchangeProperty(LBSRoute.LATITUD))
        .setHeader("long1", exchangeProperty(LBSRoute.LONGITUD))
        .log(LoggingLevel.INFO, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Coordenadas || Latitud:${headers.lat} Longitud:${headers.long1}")
        .doTry()
        	.process(exchange->{
        		InetAddress localhost = InetAddress.getLocalHost();
        		log.info("Exchange={} ||Ip local:{}",exchange.getProperty("procesoId"),localhost.getHostAddress());
        	})
        	.to("sql-stored:{{sp.obtener.ubicacion}}(FLOAT ${headers.lat},FLOAT ${headers.long1},OUT VARCHAR pais_out,OUT VARCHAR dpto_out,OUT VARCHAR ciudad_out)?dataSource=dataSourceIccid")  
       	.endDoTry()
        .doCatch(SQLException.class)
        	.log(LoggingLevel.ERROR, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Error || Detalle: ${exception.message}")
        	.setHeader(LBSRoute.HEADERS_ERROR, constant(400))
        	.throwException(SQLException.class, "{{message.response.otros}}")
        .end()
        .log(LoggingLevel.DEBUG, logger, "Exchange= ${exchangeProperty.procesoId} || mensage = Ubicacion Encontrada || respuesta :${body} ")
        .convertBodyTo(Map.class)
        .setProperty(RESPONSE_SP, body())
        .end();
        // @formatter:on
    }

}
