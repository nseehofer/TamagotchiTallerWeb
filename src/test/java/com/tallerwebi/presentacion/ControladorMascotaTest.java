package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.ServicioTemperatura;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    @Mock private HttpSession session;
    @Mock private HttpServletRequest request;
    private ServicioMascota servicioMascotaMock;
    private ServicioLogin servicioLoginMock;
    private ServicioTemperatura servicioTemperaturaMock;
    private ControladorMascota controladorMascota;
    private MascotaDTO mascotaDTOMock;

    @BeforeEach
    public void inicializar() {
        MockitoAnnotations.openMocks(this);
        servicioMascotaMock = mock(ServicioMascota.class);
        servicioLoginMock = mock(ServicioLogin.class);
        servicioTemperaturaMock = mock(ServicioTemperatura.class);
        controladorMascota = new ControladorMascota(servicioMascotaMock,servicioLoginMock,servicioTemperaturaMock);
        mascotaDTOMock = mock(MascotaDTO.class);
    }

    @Test
    public void queSePuedaCrearUnaMascotaConUnNombre() {

        // Usuario usuarioPrueba = new Usuario();
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        when(this.servicioMascotaMock.crearMascota(anyString())).thenReturn(mascotaDTOPrueba);

        when(this.request.getSession()).thenReturn(session);
        when(this.session.getAttribute("ID")).thenReturn(1L);
        // SE AGREGAN OTROS MOCK PARA CORRER EL TEST 
        when(this.session.getAttribute("EMAIL")).thenReturn("test@gmail.com");
        when(this.servicioLoginMock.buscarUsuarioPorEmail(anyString())).thenReturn(new Usuario());

        ModelAndView modelAndView = controladorMascota.crearMascota(nombreMascota,request);

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
    public void queUnUsuarioPresionaJugarYLaMascotaDisminuyaSuEnergia() throws EnergiaInsuficiente {
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        mascotaDTOPrueba.setId(1L);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDTOPrueba);

        doAnswer(invocation -> {
            MascotaDTO mascota = invocation.getArgument(0);
            mascota.setEnergia(mascota.getEnergia() - 25.00);
            return mascota;
        }).when(this.servicioMascotaMock).jugar(mascotaDTOPrueba);

        ModelAndView modelAndView = controladorMascota.jugar(mascotaDTOPrueba.getId());

        String vistaEsperada = "mascota";

        assertThat(vistaEsperada, equalTo(modelAndView.getViewName()));
    }

    @Test
    public void queUnUsuarioPresionaDormirYLaMascotaAumenteSuEnergia() throws EnergiaMaxima {
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        mascotaDTOPrueba.setId(1L);
        mascotaDTOPrueba.setEnergia(60.0);
        mascotaDTOPrueba.setFelicidad(100.0);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDTOPrueba);

        doAnswer(invocation -> {
            MascotaDTO mascota = invocation.getArgument(0);
            mascota.setEnergia(Math.min(100.00, mascota.getEnergia() + 25.00));
            mascota.setFelicidad(Math.max(0.0, mascota.getFelicidad() - 25.00));
            mascota.setUltimaSiesta(LocalDateTime.now());
            return mascota;
        }).when(this.servicioMascotaMock).dormir(mascotaDTOPrueba);

        ModelAndView modelAndView = controladorMascota.dormir(mascotaDTOPrueba.getId());

        String vistaEsperada = "mascota";

        assertThat(vistaEsperada, equalTo(modelAndView.getViewName()));
        MascotaDTO mascotaActualizada = (MascotaDTO) modelAndView.getModel().get("mascota");
        assertThat(mascotaActualizada.getUltimaSiesta(), instanceOf(LocalDateTime.class));

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
