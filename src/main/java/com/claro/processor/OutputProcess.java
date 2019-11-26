package com.claro.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.MessageHelper;
import org.springframework.stereotype.Component;

@Component
public class OutputProcess implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String output = MessageHelper.extractBodyAsString(exchange.getIn());
        String proceso = (String) exchange.getProperty("proceso");
        String mensajeRespuesta = (String) exchange.getProperty("mensajeRespuesta");
        Map<String, String> map = new HashMap<>();
        
        if (Objects.nonNull(output) && !"".equals(output)) {
            output = output.substring(1, output.length() - 1);
            String[] keyValuePairs = output.split(",");
            
            for (String pair : keyValuePairs) {
                String[] entry = pair.split("=");
                map.put(entry[0].trim(), entry[1].trim());
            }
        } else {
            throw new Exception(mensajeRespuesta);
        }
        
        exchange.getIn().setHeader("codMsg", map.get("codMsg"));
        exchange.getIn().setHeader("mensaje", map.get("mensaje"));
        
        if(proceso.equals("consultaCoordenadas")) {
            exchange.setProperty("mapCoordenadas", map);
            exchange.getIn().setHeader("longitud", map.get("longitud"));
            exchange.getIn().setHeader("latitud", map.get("latitud"));
            exchange.getIn().setHeader("fecha", map.get("fecha"));
        }else if(proceso.equals("consultaIccid")) {
            exchange.getIn().setHeader("iccid", map.get("iccid"));            
        }else if(proceso.equals("consultaUbicacion")) {
        	exchange.getIn().setHeader("iccid_cod",Integer.valueOf(map.get("iccid")) );
            
            
        }
    }

}
