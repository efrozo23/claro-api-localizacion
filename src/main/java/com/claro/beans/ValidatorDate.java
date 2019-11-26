package com.claro.beans;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.claro.routes.LBSRoute;
import com.claro.util.ConvertUtils;
import com.claro.util.UtilsClaro;

@Component
public class ValidatorDate {

	@Value("${lbs.tiempomaximo}")
	private Integer minutes;
	
	@Value("${message.response.error}")
	private String message;

	private Logger logger = LoggerFactory.getLogger(ValidatorDate.class);

	@Handler
	public void differenceMinutes(Exchange exchange) {
		Instant timeStamp= Instant.now();
		ZonedDateTime laZone= timeStamp.atZone(ZoneId.of("America/Bogota"));
		LocalDateTime dateNow = laZone.toLocalDateTime();
		String dateU = UtilsClaro.formatDate(exchange.getProperty(LBSRoute.FECHA_UBICACION, String.class));

		LocalDateTime localDateU = LocalDateTime.parse(dateU);

		logger.debug("Fecha local: {}", dateNow);
		logger.debug("Fecha Ubicacion: {}", localDateU);

		long minutesDff = ChronoUnit.MINUTES.between(localDateU, dateNow);

		logger.info("Diferencia de minutos: {}", minutesDff);
		
		dateU = dateU.replace("T", " ");
		exchange.setProperty(LBSRoute.FECHA_UBICACION, dateU);

		if (minutesDff > this.minutes) {
			logger.error("Diferencia de minutos superada: {}", minutesDff);
			exchange.setProperty(LBSRoute.AUDIT_DETAILS, "Diferencia de minutos " + this.minutes);
			throw new NullPointerException(message);
		}

	}
	
	public Double convertDecimal(String l) {
		String dir = "";
		for (int i = 0; i < l.length(); i++) {
			if(l.charAt(i)=='S' || l.charAt(i)=='s' || l.charAt(i)=='W' || l.charAt(i)=='w' || l.charAt(i)=='N' || l.charAt(i)=='n' || l.charAt(i)=='O' || l.charAt(i)=='o' || l.charAt(i)=='e' || l.charAt(i)=='E') {
				dir = String.valueOf(l.charAt(i));
			}
		}
		l = l.replace(dir, "");
		String[] data = l.split(" ");
		Long degrees = Long.valueOf(data[0]);
		Long minuts = Long.valueOf(data[1]);
		Double seconds = Double.valueOf(data[2]);
		return ConvertUtils.convertDecimal(degrees, minuts, seconds, dir);
	}
	
}
