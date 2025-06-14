package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import org.springframework.web.servlet.ModelAndView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.instanceOf;

import java.time.LocalDateTime;

public class ControladorMascotaTest {
    private ServicioMascota servicioMascotaMock;
    private ControladorMascota controladorMascota;
    private MascotaDTO mascotaDTOMock;

    @BeforeEach
    public void inicializar() {
        servicioMascotaMock = mock(ServicioMascota.class);
        controladorMascota = new ControladorMascota(servicioMascotaMock);
        mascotaDTOMock = mock(MascotaDTO.class);
    }

    @Test
    public void queSePuedaCrearUnaMascotaConUnNombre() {

        // Usuario usuarioPrueba = new Usuario();
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        when(this.servicioMascotaMock.crearMascota(anyString())).thenReturn(mascotaDTOPrueba);

        ModelAndView modelAndView = controladorMascota.crearMascota(nombreMascota);

        String vistaEsperada = "mascota";

        assertThat(vistaEsperada, equalTo(modelAndView.getViewName()));
    }

    @Test
    public void queUnUsuarioPresionaAlimentarYLaMascotaDisminuyaSuHambreYRegistreSuHorarioDeAlimentacion() {
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        mascotaDTOPrueba.setId(1L);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDTOPrueba);

        doAnswer(invocation -> {
            MascotaDTO mascota = invocation.getArgument(0);
            mascota.setHambre(mascota.getHambre() - 25.00);
            mascota.setUltimaAlimentacion(LocalDateTime.now());
            return mascota;
        }).when(this.servicioMascotaMock).alimentar(mascotaDTOPrueba);

        ModelAndView modelAndView = controladorMascota.alimentar(mascotaDTOPrueba.getId());

        String vistaEsperada = "mascota";

        assertThat(vistaEsperada, equalTo(modelAndView.getViewName()));
        assertThat(modelAndView.getModel().get("ultimaAlimentacion"), instanceOf(LocalDateTime.class));
    }

    @Test
    public void queAlLimpiarLaMascotaSeMuestreLaVistaCorrecta() throws LimpiezaMaximaException {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);
        when(this.servicioMascotaMock.limpiarMascota(mascotaDePrueba)).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.limpiarMascota(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("mascota"), equalTo(mascotaDePrueba));

    }

    @Test
    public void queAlDormirLaMascotaSeMuestreLaVistaCorrecta () throws EnergiaMaxima {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);
        when(this.servicioMascotaMock.dormir(mascotaDePrueba)).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.limpiarMascota(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("mascota"), equalTo(mascotaDePrueba));
    }

    @Test
    public void queAlJugarLaMascotaSeMuestreLaVistaCorrecta () throws EnergiaInsuficiente {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);
        when(this.servicioMascotaMock.jugar(mascotaDePrueba)).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.limpiarMascota(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("mascota"), equalTo(mascotaDePrueba));
    }





}
