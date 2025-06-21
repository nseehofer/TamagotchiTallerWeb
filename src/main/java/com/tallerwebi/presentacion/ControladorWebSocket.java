package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.excepcion.MascotaSatisfecha;

@Controller
public class ControladorWebSocket {

    private ServicioMascota servicioMascota;

    @Autowired
    public ControladorWebSocket(ServicioMascota servicioMascota) {
        this.servicioMascota = servicioMascota;
    }

    public static class MascotaDTOEscalaParaId {
        private Long id;

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
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }

    @MessageMapping("/jugar")
    @SendTo("/topic/messages")
    // RECIBO UN JSON.stringify con el id de la mascota
    public String jugarConMascotaConSocketYPersistencia(MascotaDTOEscalaParaId mascotaParaId) throws Exception {

        MascotaDTO mascota = servicioMascota.traerUnaMascota(mascotaParaId.getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            mascota = servicioMascota.jugar(mascota);
        } catch (EnergiaInsuficiente energiaInsuficiente) {
            String error = mapper.writeValueAsString(
                    "No podés jugar, te falta energía");
            return error;
        }

        String JSONMascota = mapper.writeValueAsString(mascota);

        return JSONMascota;
    }





}
