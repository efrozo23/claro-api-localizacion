package com.claro;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.claro.routes.LBSRoute;
import com.claro.util.ConvertUtils;
import com.claro.util.UtilsClaro;
import com.jayway.jsonpath.internal.Utils;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@MockEndpoints
@TestPropertySource(locations = "classpath:application.properties")
@ContextConfiguration
//@MockEndpointsAndSkip
public class ClaroUnitTest extends CamelTestSupport {

	@Produce(uri = "direct://search-ubication")
	private ProducerTemplate producerTemplate;

	@Produce(uri = "direct://search-coor", context = "CamelCxfRest")
	private ProducerTemplate producerCoor;

	@Produce(uri = "direct://search-coor", context = "CamelCxfRest")
	private ProducerTemplate producerLinear;

	@EndpointInject(uri = "mock://search-ubication")
	private MockEndpoint mockCamel;

	@EndpointInject(uri = "mock:execute-sp")
	private MockEndpoint mockCamelSP;

	@EndpointInject(uri = "mock:statusOK")
	protected MockEndpoint statusOK;

	@EndpointInject(uri = "mock:search-coor", context = "CamelCxfRest")
	protected MockEndpoint mockSearchC;

	@Produce(uri = "direct://lbs-endpoint")
	private ProducerTemplate produceTLbs;

	@EndpointInject(uri = "mock://lbs-endpoint")
	private MockEndpoint mockLBS;

	@Autowired
	private CamelContext ctx;

	@Autowired
	private Environment env;

	

	@Override
	protected CamelContext createCamelContext() throws Exception {
		return ctx;
	}

	@Test
	public void getUbicacion() throws Exception {

		Exchange exchange = new DefaultExchange(ctx);
		exchange.getIn().setHeader("lat", String.valueOf("6.511667094384407"));
		exchange.getIn().setHeader("long1", String.valueOf("-74.06697012663736"));
		exchange.setProperty(LBSRoute.LATITUD, "22 54 12.5 S");
		exchange.setProperty(LBSRoute.LONGITUD, "43 10 20.1 W");
		producerTemplate.send(exchange);

		mockCamel.assertIsSatisfied();
	}

	@Test
	public void convertLatitud() {

		long degrees = 49;
		long minutes = 19;
		double seconds = 56.2152;
		String direction = "S";

		double latitud = ConvertUtils.convertDecimal(degrees, minutes, seconds, direction);
		log.info("Coordenadas Grados M S: {}", degrees + "° " + minutes + "' " + seconds + "''");
		log.info("Coordenadas decimal:{}", latitud);
		assertEquals(Double.valueOf("-49.332282").toString(), String.valueOf(latitud));

	}

	@Test
	public void convertLongitud() {

		long degrees = 67;
		long minutes = 19;
		double seconds = 27.1884;
		String direction = "E";

		double longitud = ConvertUtils.convertDecimal(degrees, minutes, seconds, direction);

		log.info("Coordenadas Grados M S: {}", degrees + "° " + minutes + "' " + seconds + "''");
		log.info("Coordenadas decimal:{}", longitud);
		assertEquals(Double.valueOf("67.324219").toString(), String.valueOf(longitud));

	}

	@Test
	public void searchCoordinates() throws Exception {
		String xmlData = env.getProperty("xml.data");
		context().start();
		context().getRouteDefinition("SearchCoordinates").adviceWith(context(), new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				
				mockEndpointsAndSkip("direct://search-ubication");

			}
		});
		producerCoor.sendBody(LBSRoute.LBS_COORDINATES, xmlData);
		mockSearchC.assertIsSatisfied();
	}

	@Test
	public void validateLinearRing() throws InterruptedException {
		String xml = env.getProperty("xml.data.linear");
		producerLinear.sendBody(xml);
		mockSearchC.assertIsSatisfied();
	}

	@Test
	public void callLBS() throws InterruptedException {
		Exchange exch = new DefaultExchange(ctx);
		exch.setProperty("min", "573023253439");
		produceTLbs.send(exch);
		mockLBS.assertIsSatisfied();
	}

	@Test
	public void validaFecha() {

		Instant timeStamp = Instant.now();
		ZonedDateTime LAZone = timeStamp.atZone(ZoneId.of("America/Bogota"));
		LocalDateTime dateNow = LAZone.toLocalDateTime();
		log.info("Fecha Registrada:{}", dateNow);
		log.info("Fecha maquina:{}", timeStamp);
		log.info("Fecha bogotá:{}", LAZone);
		log.info("Zonas:{}", ZoneId.getAvailableZoneIds());
		assertFalse(false);
	}

	@Test
	public void validateProperties() {
		
		String prueba = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE svc_init SYSTEM \"MLP_SVC_INIT_320.DTD\\\"><svc_init ver=\"3.2.0\"><hdr ver=\"3.2.0\"><client><id>TEST_CLIENT1</id><pwd>tactical123!</pwd></client></hdr><slir ver=\"3.2.0\" res_type=\"SYNC\"><msids><msid enc=\"ASC\" type=\"MSISDN\">573023253439</msid></msids></slir></svc_init>";
		String resultado = UtilsClaro.convertBody(prueba);
		log.info(resultado);
	}

}
