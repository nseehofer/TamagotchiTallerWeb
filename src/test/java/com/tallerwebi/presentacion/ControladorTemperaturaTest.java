package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioCoordenada;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.ServicioTemperatura;
import com.tallerwebi.dominio.mapeado.Clima;
import com.tallerwebi.presentacion.ControladorTemperatura;
import com.tallerwebi.presentacion.MascotaDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class ControladorTemperaturaTest {
    private ServicioTemperatura servicioTemperaturaMock;
    private ServicioMascota servicioMascotaMock;
    private ServicioCoordenada servicioCoordenadaMock;
    private ControladorTemperatura controladorTemperatura;


    @BeforeEach
    public void inicializar() {
        servicioTemperaturaMock = mock(ServicioTemperatura.class);
        servicioMascotaMock = mock(ServicioMascota.class);
        servicioCoordenadaMock = mock(ServicioCoordenada.class);
        controladorTemperatura = new ControladorTemperatura(servicioTemperaturaMock, servicioMascotaMock, servicioCoordenadaMock);

    }

    @Test
    public void queMuestreLaVistaCorrectamente(){
        String vista = controladorTemperatura.vistaMascota();
        assertThat(vista,equalTo("mascota"));
    }

    @Test
    public void queAlObtenerLaTemperaturaSeAgregueAlModelo(){
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        Model model = new ExtendedModelMap();

        Clima climaMock = mock(Clima.class);
        when(climaMock.obtenerTemperaturaActual()).thenReturn(25.0);

        MascotaDTO mascotaMock = new MascotaDTO("Firulais");

        when(servicioTemperaturaMock.getTemperatura(requestMock)).thenReturn(climaMock);
        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascotaMock);

        String vista = controladorTemperatura.mostrarTemperatura(requestMock, model);

        assertThat(vista, equalTo("mascota"));
        assertThat(model.getAttribute("climaUrl"), equalTo(climaMock));
        assertThat(model.getAttribute("temperaturaActual"), equalTo(25.0));
        assertThat(model.getAttribute("mascota"), equalTo(mascotaMock));

    }

    @Test
    public void queAlFallarLaTemperaturaSeMuestreUnMensajeDeError() {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        Model model = new ExtendedModelMap();

        MascotaDTO mascotaMock = new MascotaDTO("Firulais");

        when(servicioTemperaturaMock.getTemperatura(requestMock)).thenReturn(null);
        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascotaMock);

        String vista = controladorTemperatura.mostrarTemperatura(requestMock, model);

        assertThat(vista, equalTo("mascota"));
        assertThat(model.getAttribute("error"), equalTo("No se pudo obtener la temperatura. Verifique las coordenadas."));
    }
}


