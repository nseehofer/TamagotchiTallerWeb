package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.ServicioTemperatura;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.dominio.excepcion.MascotaSatisfecha;
import com.tallerwebi.dominio.mapeado.Clima;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.instanceOf;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ControladorMascotaTest {
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
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
    public void queAlNoHaberEmailEnSesionElUsuarioSeaNull() {
        Long idMascota = 1L;
        MascotaDTO mascota = new MascotaDTO("Firulais");

        when(servicioMascotaMock.traerUnaMascota(idMascota)).thenReturn(mascota);
        when(servicioLoginMock.buscarUsuarioPorEmail(null)).thenReturn(null);
        when(servicioTemperaturaMock.getTemperatura(any())).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("EMAIL")).thenReturn(null);

        ModelAndView modelAndView = controladorMascota.verMascota(idMascota, request);

        assertEquals("mascota", modelAndView.getViewName());
        assertNull(modelAndView.getModel().get("usuario"));
    }

    @Test
    public void queSiNoHaySesionSeRedirijaAlLogin() {
        String nombreMascota = "Firulais";

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ID")).thenReturn(null);

        ModelAndView modelAndView = controladorMascota.crearMascota(nombreMascota, request);

        assertThat(modelAndView.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void queSiSeObtieneClimaSeAgregueAlModelo() {
        String nombreMascota = "Firulais";
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        MascotaDTO mascotaCreada = new MascotaDTO(nombreMascota);
        Clima climaMock = mock(Clima.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("ID")).thenReturn(1L);
        when(session.getAttribute("EMAIL")).thenReturn("test@gmail.com");
        when(servicioLoginMock.buscarUsuarioPorEmail(anyString())).thenReturn(usuarioMock);
        when(servicioMascotaMock.crearMascota(anyString(), anyLong())).thenReturn(mascotaCreada);
        when(servicioMascotaMock.crear(any())).thenReturn(mascotaCreada);
        when(servicioTemperaturaMock.getTemperatura(any())).thenReturn(climaMock);
        when(climaMock.obtenerTemperaturaActual()).thenReturn(23.0);

        ModelAndView modelAndView = controladorMascota.crearMascota(nombreMascota, request);

        assertEquals("mascota", modelAndView.getViewName());
        assertEquals(climaMock, modelAndView.getModel().get("climaUrl"));
        assertEquals(23.0, modelAndView.getModel().get("temperaturaActual"));
        assertNull(modelAndView.getModel().get("error"));
    }

    @Test
    public void queAlNoObtenerClimaSeMuestreMensajeDeError() {
        Long idMascota = 1L;
        String email = "usuario@email.com";
        MascotaDTO mascota = new MascotaDTO("Firulais");
        Usuario usuario = new Usuario();

        when(servicioMascotaMock.traerUnaMascota(idMascota)).thenReturn(mascota);
        when(servicioLoginMock.buscarUsuarioPorEmail(email)).thenReturn(usuario);
        when(servicioTemperaturaMock.getTemperatura(any())).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("EMAIL")).thenReturn(email);

        ModelAndView modelAndView = controladorMascota.verMascota(idMascota, request);

        assertEquals("mascota", modelAndView.getViewName());
        assertEquals("No se pudo obtener la temperatura. Verifique las coordenadas.", modelAndView.getModel().get("error"));
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
    public void queAlAlimentarLaMascotaConHambreSuficienteSeMuestreElMensajeDeError() {
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        mascotaDTOPrueba.setId(1L);
        mascotaDTOPrueba.setHambre(0.0);

        when(servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDTOPrueba);
        when(servicioMascotaMock.alimentar(any())).thenThrow(new MascotaSatisfecha("Tu mascota está satisfecha"));

        ModelAndView modelAndView = controladorMascota.alimentar(mascotaDTOPrueba.getId());

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("error"), equalTo("Tu mascota está satisfecha"));
    }

    @Test
    public void queAlJugarLaMascotaConEnergiaInsuficienteSeMuestreElMensajeDeError() throws EnergiaInsuficiente {
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        mascotaDTOPrueba.setId(1L);
        mascotaDTOPrueba.setEnergia(0.0);

        when(servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDTOPrueba);
        when(servicioMascotaMock.jugar(any())).thenThrow(new EnergiaInsuficiente("No podés jugar, te falta energía"));

        ModelAndView modelAndView = controladorMascota.jugar(mascotaDTOPrueba.getId());

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("error"), equalTo("No podés jugar, te falta energía"));
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
    public void queAlDormirLaMascotaConEnergiaMaximaSeMuestreElMensajeDeError() throws EnergiaMaxima {
        String nombreMascota = "Firulais";
        MascotaDTO mascotaDTOPrueba = new MascotaDTO(nombreMascota);
        mascotaDTOPrueba.setId(1L);
        mascotaDTOPrueba.setEnergia(100.0);

        when(servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDTOPrueba);
        when(servicioMascotaMock.dormir(any())).thenThrow(new EnergiaMaxima("No se puede dormir porque no tiene sueño"));

        ModelAndView modelAndView = controladorMascota.dormir(mascotaDTOPrueba.getId());

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("error"), equalTo("No se puede dormir porque no tiene sueño"));
    }

    @Test
    public void queAlLimpiarLaMascotaConLimpiezaMaximaSeMuestreElMensajeDeError() throws LimpiezaMaximaException {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);
        mascotaDePrueba.setHigiene(100.0);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);
        when(this.servicioMascotaMock.limpiarMascota(any())).thenThrow(new LimpiezaMaximaException("La higiene se encuentra al maximo"));

        ModelAndView modelAndView = controladorMascota.limpiarMascota(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("error"), equalTo("La higiene se encuentra al maximo"));
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
    public void queAlDormirLaMascotaSeMuestreLaVistaCorrecta() throws EnergiaMaxima {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);
        when(this.servicioMascotaMock.dormir(mascotaDePrueba)).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.dormir(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("mascota"), equalTo(mascotaDePrueba));
    }

    @Test
    public void queAlJugarLaMascotaSeMuestreLaVistaCorrecta() throws EnergiaInsuficiente {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);
        when(this.servicioMascotaMock.jugar(mascotaDePrueba)).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.jugar(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("mascota"));
        assertThat(modelAndView.getModel().get("mascota"), equalTo(mascotaDePrueba));
    }

    @Test
    public void queAlVerUnaMascotaMuertaSeMuestreLaVistaCorrecta() {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);
        mascotaDePrueba.setEstaVivo(false);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.verMascotaMuerta(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("cementerio"));
        assertThat(modelAndView.getModel().get("mascota"), equalTo(mascotaDePrueba));
    }

    @Test
    public void queAlVerUnaMascotaVivaSeRedirijaAlHome() {
        Long idMascota = 1L;
        MascotaDTO mascotaDePrueba = new MascotaDTO("Firulais");
        mascotaDePrueba.setId(idMascota);
        mascotaDePrueba.setEstaVivo(true);

        when(this.servicioMascotaMock.traerUnaMascota(anyLong())).thenReturn(mascotaDePrueba);

        ModelAndView modelAndView = controladorMascota.verMascotaMuerta(idMascota);

        assertThat(modelAndView.getViewName(), equalTo("redirect:/home"));
    }

    @Test
    public void queAlVerUnaMascotaSeMuestreCorrectamenteConClima() {
        Long idMascota = 1L;
        String email = "usuario@email.com";
        MascotaDTO mascota = new MascotaDTO("Firulais");
        Usuario usuario = new Usuario();
        Clima climaMock = mock(Clima.class);

        when(servicioMascotaMock.traerUnaMascota(idMascota)).thenReturn(mascota);
        when(servicioLoginMock.buscarUsuarioPorEmail(email)).thenReturn(usuario);
        when(servicioTemperaturaMock.getTemperatura(any())).thenReturn(climaMock);
        when(climaMock.obtenerTemperaturaActual()).thenReturn(22.5);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("EMAIL")).thenReturn(email);

        ModelAndView modelAndView = controladorMascota.verMascota(idMascota, request);

        assertEquals("mascota", modelAndView.getViewName());
        assertEquals(mascota, modelAndView.getModel().get("mascota"));
        assertEquals(usuario, modelAndView.getModel().get("usuario"));
        assertEquals(climaMock, modelAndView.getModel().get("climaUrl"));
        assertEquals(22.5, modelAndView.getModel().get("temperaturaActual"));
    }

    @Test
    public void queSeMuestreElListadoDeMascotas() {
        Mascota mascota1 = new Mascota();
        Mascota mascota2 = new Mascota();

        List<Mascota> mascotas = Arrays.asList(mascota1, mascota2);

        when(servicioMascotaMock.traerMascotas()).thenReturn(mascotas);

        ModelAndView modelAndView = controladorMascota.mostrarListadoDeMascotas();

        assertEquals("inicio", modelAndView.getViewName());
        assertEquals(mascotas, modelAndView.getModel().get("mascotas"));
    }

}
