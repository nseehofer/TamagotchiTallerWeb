package com.tallerwebi.dominio.mapeado;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Clima {

    @JsonProperty("timezone")
    private String zonaHoraria;

    @JsonProperty("timezone_abbreviation")
    private String zonaHorariaAbreviada;

    @JsonProperty("hourly")
    private TemperaturaPorHora temperaturaPorHora;

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public String getZonaHorariaAbreviada() {
        return zonaHorariaAbreviada;
    }

    public TemperaturaPorHora getTemperaturaPorHora() {
        return temperaturaPorHora;
    }

    public Double obtenerTemperaturaActual() {
        int horaActual = LocalTime.now().getHour(); // 0 a 23
        List<Double> temperaturas = temperaturaPorHora.getTemperaturaPorOrdenHorario();
        return (temperaturas != null && horaActual < temperaturas.size()) ? temperaturas.get(horaActual) : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TemperaturaPorHora {

        @JsonProperty("temperature_2m")
        private List<Double> temperaturaPorOrdenHorario;

        public List<Double> getTemperaturaPorOrdenHorario() {
            return temperaturaPorOrdenHorario;
        }
    }
}