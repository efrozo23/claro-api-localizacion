package com.claro.dto.response;


import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class Respuesta {
    @JsonProperty
    @ApiModelProperty(dataType = "String")
    private String codigoRespuesta;
    @JsonProperty
    @ApiModelProperty(dataType = "String")
    private String mensajeRespuesta;

    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }

}
