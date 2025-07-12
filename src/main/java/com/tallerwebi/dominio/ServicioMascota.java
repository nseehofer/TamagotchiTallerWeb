package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.mapeado.Clima;
import com.tallerwebi.presentacion.MascotaDTO;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface ServicioMascota {
    MascotaDTO crear(MascotaDTO mascota);
    List<Mascota> traerMascotas();
    List<Mascota> traerMascotasDeUnUsuario(Long idUsuario);
    MascotaDTO traerUnaMascota(Long id);
    void actualizarMascota(MascotaDTO mascota);

    MascotaDTO crearMascota(String nombre);
    MascotaDTO crearMascota(String nombre, Long idUsuario);
    MascotaDTO jugar(MascotaDTO mascota);////A borrar
    MascotaDTO alimentar(MascotaDTO mascota) throws MonedasInsuficientesException;

    MascotaDTO limpiarMascota(MascotaDTO mascotaDTO) throws LimpiezaMaximaException, MonedasInsuficientesException;

    MascotaDTO actualizarEstadisticas(MascotaDTO mascotaDTO, LocalDateTime horaActual) throws MascotaMuertaException, MascotaDespiertaException;

    MascotaDTO dormir(MascotaDTO mascota) throws EnergiaMaxima;

    Boolean chequearSiLaMascotaSeEnferma(MascotaDTO mascota);

    MascotaDTO curarMascota(MascotaDTO mascota) throws MascotaSanaException, MonedasInsuficientesException;

    MascotaDTO chequearSiLaMascotaSigueViva(MascotaDTO mascota) throws MascotaMuertaException;

    MascotaDTO abrigar (MascotaDTO mascota) throws MascotaAbrigadaException;
    MascotaDTO desabrigar (MascotaDTO mascota) throws MascotaDesabrigadaException;

    void siHaceFrioYLaMascotaEstaDesabrigadaSePuedeEnfermarConMayorProbabilidad(Clima clima, MascotaDTO mascota);

    MascotaDTO despertar(MascotaDTO mascota) throws MascotaDespiertaException;

    Boolean chequearSiAlcanzanLasmonedasParaLaAccion(MascotaDTO mascota, Double costo) throws MonedasInsuficientesException;
}


