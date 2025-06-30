package com.tallerwebi.dominio.implementacion;

import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.MascotaSatisfecha;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.presentacion.MascotaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioMascotaImp implements ServicioMascota {

    private RepositorioMascota repositorioMascota;

    @Autowired
    public ServicioMascotaImp(RepositorioMascota repositorioMascota) {
        this.repositorioMascota = repositorioMascota;
    }

    @Override
    public MascotaDTO crear(MascotaDTO mascota) {
        MascotaDTO mascotaCreada = this.traerUnaMascota(this.repositorioMascota.crear(mascota.obtenerEntidad()));
        return mascotaCreada;
    }

    @Override
    public List<Mascota> traerMascotas() {
        return this.repositorioMascota.obtenerListaDeMascotas();
    }

    @Override
    public List<Mascota> traerMascotasDeUnUsuario(Long idUsuario) {
        return this.repositorioMascota.obtenerListaDeMascotasDeUnUsuario(idUsuario);
    }

    @Override
    public MascotaDTO traerUnaMascota(Long id) {

        MascotaDTO mascota = new MascotaDTO(this.repositorioMascota.obtenerPor(id));
        return mascota;
    }

    @Override
    public void actualizarMascota(MascotaDTO mascota) {
        this.repositorioMascota.actualizar(mascota.obtenerEntidad());
    }

    @Override
    public MascotaDTO crearMascota(String nombre) {
            return new MascotaDTO(nombre);
    }

    public MascotaDTO crearMascota(String nombre, Long idUsuario) {
        return new MascotaDTO(nombre,idUsuario);
    }

    public MascotaDTO jugar(MascotaDTO mascota) {
        Double energiaADescontarPorJuego = 25.00;
        Double energiaActual = mascota.getEnergia();
        Double higieneActual = mascota.getHigiene();
        Double felicidadActual = mascota.getFelicidad();

        if (energiaActual >= energiaADescontarPorJuego) {
            mascota.setEnergia(energiaActual - energiaADescontarPorJuego);
            mascota.setHigiene(higieneActual - 25.0);
            mascota.setFelicidad(Math.min(100.00, felicidadActual + 25.0));
            //actualizamos en base de datos
            this.actualizarMascota(mascota);
        } else {
            throw new EnergiaInsuficiente("No podés jugar, te falta energía");
        }

        return mascota;
    }

    @Override
    public MascotaDTO dormir(MascotaDTO mascota) throws EnergiaMaxima {
        Double energiaASumar = 25.00;
        Double energiaActual = mascota.getEnergia();
        Double felicidadARestar = 25.00;
        Double felicidadActual = mascota.getFelicidad();

        if (energiaActual >= 100.00)
            throw new EnergiaMaxima("No se puede dormir porque no tiene sueño");

        mascota.setEnergia(Math.min(100.00, energiaActual + energiaASumar));
        mascota.setFelicidad(Math.max(0.0, felicidadActual - felicidadARestar));
        mascota.setUltimaSiesta(LocalDateTime.now());
        //actualizamos en base de datos
        this.actualizarMascota(mascota);

        return mascota;
    }

    public MascotaDTO alimentar(MascotaDTO mascota) {
        Double hambreASumarPorJuego = 25.00;
        Double hambreActual = mascota.getHambre();
        if (hambreActual < 100.0) {
            mascota.setHambre(Math.min(100.0, hambreActual + hambreASumarPorJuego));
            mascota.setUltimaAlimentacion(LocalDateTime.now());
            //actualizamos en base de datos
            this.actualizarMascota(mascota);
        } else {
            throw new MascotaSatisfecha("Tu mascota está satisfecha");
        }

        return mascota;
    }

    @Override
    public MascotaDTO limpiarMascota(MascotaDTO mascota) throws LimpiezaMaximaException {
        if(mascota.getHigiene() == 100.0) {
            throw new LimpiezaMaximaException("La higiene ya se encuentra al máximo");
        } else {
            mascota.setHigiene(100.0);
            mascota.setUltimaHigiene(LocalDateTime.now());
            this.actualizarMascota(mascota);
            return mascota;
        }
    }

    @Override
    public MascotaDTO actualizarEstadisticas(MascotaDTO mascota, LocalDateTime horaActual) {
        double disminucionHigiene = (double) Duration.between(mascota.getUltimaHigiene(), horaActual).toMinutes() * 12.87;
        double disminucionHambre = (double) Duration.between(mascota.getUltimaAlimentacion(), horaActual).toMinutes() * 15.19;
        double disminucionEnergia = (double) Duration.between(mascota.getUltimaSiesta(), horaActual).toMinutes() * 17.24;

        double higieneActual = mascota.getHigiene() - disminucionHigiene;
        double hambreActual = mascota.getHambre() - disminucionHambre;
        double energiaActual = mascota.getEnergia() - disminucionEnergia;
        double felicidadActual = (higieneActual + hambreActual + energiaActual) / 3.0;

        mascota.setHigiene(Math.max(higieneActual, 0.0));
        mascota.setHambre(Math.max(hambreActual, 0.0));
        mascota.setEnergia(Math.max(energiaActual, 0.0));
        mascota.setFelicidad(Math.max(felicidadActual, 0.0));

        this.actualizarMascota(mascota);

        return mascota;

    }

    @Override
    public MascotaDTO chequearSalud(MascotaDTO mascota) {
        Double hambre = mascota.getHambre();
        Double energia = mascota.getEnergia();
        Double felicidad = mascota.getFelicidad();
        Double higiene = mascota.getHigiene();

        Double promedioEstadisticas = (hambre + energia + felicidad + higiene) / 4.0;
        Double riesgoAEnfermar = 1.0 - (promedioEstadisticas / 100);

        Double random = obtenerRandom();

        mascota.setEstaEnfermo(random < riesgoAEnfermar);

        this.actualizarMascota(mascota);

        return mascota;
    }

    public Double obtenerRandom() {
        return Math.random();
    }
}
