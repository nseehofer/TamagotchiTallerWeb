package com.tallerwebi.dominio.implementacion;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.ServicioCoordenada;
import com.tallerwebi.dominio.mapeado.Clima;
import com.tallerwebi.dominio.mapeado.Coordenada;

@Service
@Transactional
public class ServicioCoordenadaImp implements ServicioCoordenada{


    
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Autowired
    public ServicioCoordenadaImp(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Coordenada obtenerCoordenadas(String pais, String provincia) {
        String url = String.format("https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(provincia, StandardCharsets.UTF_8) + "," + URLEncoder.encode(pais, StandardCharsets.UTF_8) +"&format=json");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // MAPEO DE DATOS
                Coordenada[] resultado = objectMapper.readValue(response.body(), Coordenada[].class);
                return resultado.length> 0 ? resultado[0] : null;
            } else {
                return null;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
