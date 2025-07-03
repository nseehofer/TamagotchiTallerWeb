package com.tallerwebi.dominio.implementacion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.ServicioCoordenada;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioTemperatura;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.mapeado.Clima;
import com.tallerwebi.dominio.mapeado.Coordenada;

@Service
@Transactional
public class ServicioTemperaturaImp implements ServicioTemperatura{
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private ServicioCoordenada servicioCoordenada;
    private ServicioLogin servicioLogin;

    @Autowired
    public ServicioTemperaturaImp(HttpClient httpClient, ObjectMapper objectMapper, ServicioCoordenada servicioCoordenada, ServicioLogin servicioLogin) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.servicioCoordenada = servicioCoordenada;
        this.servicioLogin = servicioLogin;
    }

    @Override
    // SESION POR PARAMETRO PARA OBTENER EMAIL DEL USUARIO
    public Clima getTemperatura(HttpServletRequest sessionUsuario) {
        
        
        String emailUsuaioEnSesion = (String) sessionUsuario.getSession().getAttribute("EMAIL");
        // OBTENER USUARIO EN SESION
        Usuario usuarioObtenido = this.servicioLogin.buscarUsuarioPorEmail(emailUsuaioEnSesion);
        
        //OBTENER COORDENADAS DEL USUARIO USANDO OTRA API
        Coordenada coordenadaDeUsuario = this.servicioCoordenada.obtenerCoordenadas(usuarioObtenido.getPais(), usuarioObtenido.getProvincia());

        //API PARA OBTENER TEMPERATURA
        String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=" + coordenadaDeUsuario.getLatitud() + "9&longitude=" +  coordenadaDeUsuario.getLongitud() + "&hourly=temperature_2m&timezone=auto&forecast_days=1");
        
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
