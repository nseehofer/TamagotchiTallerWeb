package com.tallerwebi.dominio;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.mapeado.Clima;

@Service
@Transactional
public class ServicioTemperaturaImp implements ServicioTemperatura{
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ServicioTemperaturaImp(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Clima getTemperatura(Double latitud, Double longitud) {
        // Implementación del método para obtener la temperatura
        // Aquí se puede usar httpClient y objectMapper para hacer una solicitud a una API externa
        // y procesar la respuesta.
        
        // Por ejemplo:
        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=" + latitud + "9&longitude=" +  longitud + "&hourly=temperature_2m&timezone=auto&forecast_days=1");
         HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // MAPEO DE DATOS
                Clima resultado = objectMapper.readValue(response.body(), Clima.class);
                return resultado != null ? resultado : null;
            } else {
                return null;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
