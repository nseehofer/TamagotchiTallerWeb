package com.tallerwebi.presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.excepcion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tallerwebi.presentacion.ControladorWebSocket.MascotaDTOEscalaParaId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;


public class ControladorWebSocketTest {
    private ServicioMascota servicioMascotaMock;
    private ControladorWebSocket controladorWebSocket;

    @BeforeEach
    public void inicializar() {
        servicioMascotaMock = mock(ServicioMascota.class);
        controladorWebSocket = new ControladorWebSocket(servicioMascotaMock);
    }
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void queSePuedaAlimentarConWebSocketYPersistencia() throws Exception, MonedasInsuficientesException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.alimentar(mascota)).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.alimentarMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queNoSePuedaAlimentarConWebSocketYPersistenciaSiEstaSatisfecha() throws Exception, MonedasInsuficientesException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.alimentar(mascota)).thenThrow(new MascotaSatisfecha("Tu mascota está satisfecha"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.alimentarMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString("Tu mascota está satisfecha"), respuesta);
    }

    @Test
    public void queSePuedaLimpiarConWebSocketYPersistencia() throws Exception, LimpiezaMaximaException, MonedasInsuficientesException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.limpiarMascota(mascota)).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.limpiarMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queNoSePuedaLimpiarConWebSocketYPersistenciaSiEstaLimpia() throws Exception, LimpiezaMaximaException, MonedasInsuficientesException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.limpiarMascota(mascota)).thenThrow(new LimpiezaMaximaException("La higiene ya se encuentra al máximo"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.limpiarMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString("La higiene ya se encuentra al máximo"), respuesta);
    }

    @Test
    public void queSePuedaJugarConWebSocketYPersistencia() throws Exception {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.jugar(mascota)).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.jugarConMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queNoSePuedaJugarConWebSocketYPersistenciaSiEstaCansada() throws Exception {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.jugar(mascota)).thenThrow(new EnergiaInsuficiente("No podés jugar, te falta energía"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.jugarConMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString("No podés jugar, te falta energía"), respuesta);
    }

    @Test
    public void queSePuedaDormirConWebSocketYPersistencia() throws Exception, EnergiaMaxima {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.dormir(mascota)).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.dormirConMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queNoSePuedaDormirConWebSocketYPersistenciaSiEstaDescansada() throws Exception, EnergiaMaxima {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.dormir(mascota)).thenThrow(new EnergiaMaxima("No se puede dormir porque no tiene sueño"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.dormirConMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString("No se puede dormir porque no tiene sueño"), respuesta);
    }

    @Test
    public void queSePuedaActualizarDatosMascotaYPersistencia() throws Exception, MascotaMuertaException, MascotaDespiertaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.actualizarEstadisticas(eq(mascota), any(LocalDateTime.class))).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.actualizarDatosMascotaYPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queSePuedaCurarMascotaYPersistencia() throws Exception, MascotaSanaException, MonedasInsuficientesException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.curarMascota(eq(mascota))).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.curarConMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queNoSePuedaCurarMascotaSanaYPersistencia() throws Exception, MascotaSanaException, MonedasInsuficientesException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.curarMascota(eq(mascota))).thenThrow(new MascotaSanaException(("La mascota no esta enferma")));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.curarConMascotaConSocketYPersistencia(dto);

        assertEquals(mapper.writeValueAsString("La mascota no esta enferma"), respuesta);
    }

    @Test
    public void queSePuedaDespertarMascotaYPersistencia() throws Exception, MascotaDespiertaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.despertar(eq(mascota))).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.despertarMascotaConPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queNoSePuedaDespertarMascotaSiYaEstaDespiertaYPersistencia() throws Exception, MascotaDespiertaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.despertar(eq(mascota))).thenThrow(new MascotaDespiertaException("La mascota ya esta despierta"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.despertarMascotaConPersistencia(dto);

        assertEquals(mapper.writeValueAsString("La mascota ya esta despierta"), respuesta);
    }
    
    @Test
    public void queSePuedaAbrigarConWebSocketYPersistencia() throws Exception, MascotaAbrigadaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.abrigar(mascota)).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.abrigarMascotaConPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }

    @Test
    public void queSePuedaDesabrigarConWebSocketYPersistencia() throws Exception, MascotaDesabrigadaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.desabrigar(mascota)).thenReturn(mascota);

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.desabrigarMascotaConPersistencia(dto);

        assertEquals(mapper.writeValueAsString(mascota), respuesta);
    }


    @Test
    public void queNoSePuedaAbrigarConWebSocketYPersistencia() throws Exception, MascotaAbrigadaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.abrigar(mascota)).thenThrow(new MascotaAbrigadaException("La mascota ya esta abrigada"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);

        String respuesta = controladorWebSocket.abrigarMascotaConPersistencia(dto);

        assertEquals(mapper.writeValueAsString("La mascota ya esta abrigada"), respuesta);
    }

    @Test
    public void queNoSePuedaDesabrigarConWebSocketYPersistencia() throws Exception, MascotaDesabrigadaException {
        MascotaDTO mascota = new MascotaDTO("Firulais");
        mascota.setId(1L);

        when(servicioMascotaMock.traerUnaMascota(1L)).thenReturn(mascota);
        when(servicioMascotaMock.desabrigar(mascota)).thenThrow(new MascotaDesabrigadaException("La mascota ya esta desabrigada"));

        MascotaDTOEscalaParaId dto = new MascotaDTOEscalaParaId(1L);
        // REVISAR COMO QUEDO EL METODO EN EL CONTROLADOR, ESTOY LANZANDO MAL LA EXCEPCION Y POR ESO NO PASA EL TEST
        String respuesta = controladorWebSocket.desabrigarMascotaConPersistencia(dto);

        assertEquals(mapper.writeValueAsString("La mascota ya esta desabrigada"), respuesta);
    }


}
