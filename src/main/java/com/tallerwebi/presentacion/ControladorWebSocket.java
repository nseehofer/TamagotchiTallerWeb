package com.tallerwebi.presentacion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tallerwebi.dominio.excepcion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.ServicioMascota;

import java.time.LocalDateTime;

@Controller
public class ControladorWebSocket {

    private ServicioMascota servicioMascota;

    @Autowired
    public ControladorWebSocket(ServicioMascota servicioMascota) {
        this.servicioMascota = servicioMascota;
    }

    public static class MascotaDTOEscalaParaId {
        private Long id;
        private Boolean estaJugando;
        private String resultado;

        public MascotaDTOEscalaParaId() {
            // Constructor vacío necesario para la deserialización
        }

        public MascotaDTOEscalaParaId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Boolean getEstaJugando() {
            return this.estaJugando;
        }

        public void setEstaJugando(Boolean estaJugando) {
            this.estaJugando = estaJugando;
        }

        public String getResultado() {
            return this.resultado;
        }
        public void setResultado(String resultado) {
            this.resultado = resultado;
        }
    }

    @MessageMapping("/alimentar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String alimentarMascotaConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.alimentar(mascota);
        } catch (MascotaSatisfecha mascotaSatisfecha) {
            String error = mapper.writeValueAsString(
                    "Tu mascota está satisfecha");
            return error;
        } catch (MonedasInsuficientesException monedasInsuficientesException) {
            String error = mapper.writeValueAsString(
                    "No te alcanzan las monedas, juga para ganar mas!");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/limpiar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String limpiarMascotaConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.limpiarMascota(mascota);
        } catch (LimpiezaMaximaException limpiezaMaxima) {
            String error = mapper.writeValueAsString(
                    "La higiene ya se encuentra al máximo");
            return error;
        } catch (MonedasInsuficientesException monedasInsuficientesException) {
            String error = mapper.writeValueAsString(
                    "No te alcanzan las monedas, juga para ganar mas!");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/jugar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota

    // AGREGARLE EL PARAMETRO RESULTADO PARA EL JUEGO "Integer resultado"
    public String jugarConMascotaConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId)
            throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            // RESOLVERLO CON UN SWITCH DENTRO DE "evaluarResultadoMinijuego"
            // servicioMascota.evaluarResultadoMinijuego(String resultado, String
            // interaccion)
            // resolver dentro del servicio con "5 if"
            mascota = servicioMascota.jugar(mascota);

        } catch (EnergiaInsuficiente energiaInsuficiente) {
            String error = mapper.writeValueAsString(
                    "No podés jugar, te falta energía");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/dormir")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String dormirConMascotaConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.dormir(mascota);
        } catch (EnergiaMaxima energiaMaxima) {
            String error = mapper.writeValueAsString(
                    "No se puede dormir porque no tiene sueño");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/curarMascota")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String curarConMascotaConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.curarMascota(mascota);
        } catch (MascotaSanaException mascotaSana) {
            String error = mapper.writeValueAsString(
                    "La mascota no esta enferma");
            return error;
        } catch (MonedasInsuficientesException monedasInsuficientesException) {
            String error = mapper.writeValueAsString(
                    "No te alcanzan las monedas, juga para ganar mas!");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/actualizar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String actualizarDatosMascotaYPersistencia(MascotaDTOEscalaParaId mascotaParaId)
            throws Exception, MascotaMuertaException, MascotaDespiertaException {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();

        mascota = servicioMascota.actualizarEstadisticas(mascota, LocalDateTime.now());

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/abrigar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String abrigarMascotaConPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.abrigar(mascota);
        } catch (MascotaAbrigadaException mascotaAbrigadaException) {
            String error = mapper.writeValueAsString(
                    "La mascota ya esta abrigada");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/desabrigar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String desabrigarMascotaConPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.desabrigar(mascota);
        } catch (MascotaDesabrigadaException mascotaAbrigadaException) {
            String error = mapper.writeValueAsString(
                    "La mascota ya esta desabrigada");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/despertar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String despertarMascotaConPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.despertar(mascota);
        } catch (MascotaDespiertaException mascotaDespiertaException) {
            String error = mapper.writeValueAsString(
                    "La mascota ya esta despierta");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/establecerSiEstaJugando")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String establecerSiEstaJugandoConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        mascota.setEstaJugando(mascotaParaId.getEstaJugando());

        servicioMascota.actualizarMascota(mascota);

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    /* METODO QUE EVALUA RESULTADO */

    @MessageMapping("/evaluarResultado")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String evaluarResultado(MascotaDTOEscalaParaId mascotaParaId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());
        String resultado = mascotaParaId.getResultado();
       
        if ("positivo".equals(resultado)) {
            mascota.setMonedas(mascota.getMonedas() + 50.0);
        } else if ("regular".equals(resultado)) {
            mascota.setMonedas(mascota.getMonedas() + 25.0);
        }
        servicioMascota.actualizarMascota(mascota);

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

}
