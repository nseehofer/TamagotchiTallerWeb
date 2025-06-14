package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.presentacion.MascotaDTO;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface ServicioMascota {
    MascotaDTO crear(MascotaDTO mascota);
    List<Mascota> traerMascotas();
    MascotaDTO traerUnaMascota(Long id);
    void actualizarMascota(MascotaDTO mascota);

    MascotaDTO crearMascota(String nombre);
    MascotaDTO jugar(MascotaDTO mascota);////A borrar
    MascotaDTO alimentar(MascotaDTO mascota);

    MascotaDTO limpiarMascota(MascotaDTO mascotaDTO) throws LimpiezaMaximaException;

    MascotaDTO actualizarHigiene(MascotaDTO mascotaDTO, LocalDateTime horaActual);

    MascotaDTO dormir(MascotaDTO mascota) throws EnergiaMaxima; ////A borrar
}


