package com.claro.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.claro.dto.response.Data;
import com.claro.dto.response.Respuesta;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Assert Solutions S.A.S
 *
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object")
public class Response implements Serializable {

    private static final long serialVersionUID = -6104876573750302537L;

    private Data data;
    private Respuesta respuesta;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Respuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Respuesta respuesta) {
        this.respuesta = respuesta;
    }

}
