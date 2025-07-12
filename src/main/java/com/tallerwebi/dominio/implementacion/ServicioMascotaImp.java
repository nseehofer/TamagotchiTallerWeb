package com.tallerwebi.dominio.implementacion;

import com.tallerwebi.dominio.RandomProvider;
import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.MascotaSatisfecha;
import com.tallerwebi.dominio.mapeado.Clima;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.dominio.excepcion.MascotaAbrigadaException;
import com.tallerwebi.dominio.excepcion.MascotaDesabrigadaException;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.presentacion.MascotaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ServicioMascotaImp implements ServicioMascota {

    private RepositorioMascota repositorioMascota;
    private RandomProvider randomProvider;

    @Autowired
    public ServicioMascotaImp(RepositorioMascota repositorioMascota, RandomProvider randomProvider) {
        this.repositorioMascota = repositorioMascota;
        this.randomProvider = randomProvider;
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
        mascota.setMonedas(this.repositorioMascota.traerMonedasPorIDMascota(id));
        return mascota;
    }

    @Override
    public void actualizarMascota(MascotaDTO mascota) {

        this.repositorioMascota.actualizar(mascota.obtenerEntidad());
        this.repositorioMascota.actualizarMonedas(mascota.getMonedas(),mascota.getId());
    }

    @Override
    public MascotaDTO crearMascota(String nombre) {
        return new MascotaDTO(nombre);
    }

    public MascotaDTO crearMascota(String nombre, Long idUsuario) {
        return new MascotaDTO(nombre, idUsuario);
    }

    public MascotaDTO jugar(MascotaDTO mascota) {
        Double energiaADescontarPorJuego = 25.00;
        Double energiaActual = mascota.getEnergia();
        Double higieneActual = mascota.getHigiene();
        Double hambreActual = mascota.getHambre();
        Double felicidadActual = mascota.getFelicidad();

        if (energiaActual >= energiaADescontarPorJuego) {
            mascota.setEnergia(energiaActual - energiaADescontarPorJuego);
            mascota.setHigiene(Math.max((higieneActual - 25.0), 0.0));
            mascota.setFelicidad(this.acotarDecimal((hambreActual + energiaActual + higieneActual) / 3.0));
            //actualizamos en base de datos
            mascota.setFelicidad(Math.min(100.00, felicidadActual + 25.0));
            // actualizamos en base de datos
            this.actualizarMascota(mascota);
        } else {
            throw new EnergiaInsuficiente("No podés jugar, te falta energía");
        }

        return mascota;
    }

    @Override
    public MascotaDTO dormir(MascotaDTO mascota) throws EnergiaMaxima {
        Double energiaActual = mascota.getEnergia();
        Double higieneActual = mascota.getHigiene();
        Double hambreActual = mascota.getHambre();

        if (energiaActual >= 100.00) {
            throw new EnergiaMaxima("No se puede dormir porque no tiene sueño");
        }
        mascota.setEstaDormido(true);
        mascota.setFelicidad(this.acotarDecimal((hambreActual + energiaActual + higieneActual) / 3.0));
        mascota.setUltimaSiesta(LocalDateTime.now());
        //actualizamos en base de datos
        this.actualizarMascota(mascota);

        return mascota;
    }

    public MascotaDTO alimentar(MascotaDTO mascota) throws MonedasInsuficientesException {
        Double hambreASumarPorJuego = 25.00;
        Double hambreActual = mascota.getHambre();
        Double higieneActual = mascota.getHigiene();
        Double energiaActual = mascota.getEnergia();

        if (hambreActual < 100.0 && this.chequearSiAlcanzanLasmonedasParaLaAccion(mascota,25.00)) {
            mascota.setHambre(Math.min(100.0, hambreActual + hambreASumarPorJuego));
            mascota.setUltimaAlimentacion(LocalDateTime.now());
            mascota.setFelicidad(this.acotarDecimal((hambreActual + energiaActual + higieneActual) / 3.0));
            mascota.setMonedas(mascota.getMonedas() - 25.00);
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
            mascota.setFelicidad(this.acotarDecimal((mascota.getHambre() + mascota.getEnergia() + mascota.getHigiene()) / 3.0));
            mascota.setUltimaHigiene(LocalDateTime.now());
            this.actualizarMascota(mascota);
            return mascota;
        }
    }

    @Override
    public MascotaDTO curarMascota(MascotaDTO mascota) throws MascotaSanaException, MonedasInsuficientesException {
        if (!mascota.getEstaEnfermo()) {
            throw new MascotaSanaException("La mascota no esta enferma");
        }

        if(this.chequearSiAlcanzanLasmonedasParaLaAccion(mascota,25.00)){
            mascota.setEstaEnfermo(false);
            this.actualizarMascota(mascota);
            return mascota;
        }else{
            throw new MonedasInsuficientesException("Monedas insuficientes, juga para ganar mas!");
        }

    }

    @Override
    public MascotaDTO actualizarEstadisticas(MascotaDTO mascota, LocalDateTime horaActual) throws MascotaMuertaException, MascotaDespiertaException {
        if(mascota.getEstaDormido()){
            double disminucionHigiene = (double) Duration.between(mascota.getUltimaHigiene(), horaActual).toSeconds() * 0.10;
            double disminucionHambre = (double) Duration.between(mascota.getUltimaAlimentacion(), horaActual).toSeconds() * 0.12;
            double aumentoEnergia = (double) Duration.between(mascota.getUltimaSiesta(), horaActual).toSeconds() * 0.69;

            double higieneActual = mascota.getHigiene() - disminucionHigiene;
            double hambreActual = mascota.getHambre() - disminucionHambre;
            double energiaActual = mascota.getEnergia() + aumentoEnergia;

            double promedioDeEstadisticas = (higieneActual + hambreActual + energiaActual) / 3.0;
            double fluctuacionSalud = ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
            double saludActual = this.acotarDecimal(promedioDeEstadisticas + fluctuacionSalud); //

            double felicidadActual = this.acotarDecimal(promedioDeEstadisticas); //promedio de higiene, hambre y energia

            mascota.setHigiene(Math.max(higieneActual, 0.0));
            mascota.setHambre(Math.max(hambreActual, 0.0));
            mascota.setEnergia(Math.min(energiaActual, 100.0));
            mascota.setFelicidad(Math.max(felicidadActual, 0.0));
            mascota.setSalud(Math.max(saludActual, 0.0));

            if(mascota.getEnergia() >= 100.0){
                this.despertar(mascota);
            }

            this.actualizarMascota(mascota);

            return mascota;

        } else {
            double disminucionHigiene = (double) Duration.between(mascota.getUltimaHigiene(), horaActual).toSeconds() * 0.21;
            double disminucionHambre = (double) Duration.between(mascota.getUltimaAlimentacion(), horaActual).toSeconds() * 0.25;
            double disminucionEnergia = (double) Duration.between(mascota.getUltimaSiesta(), horaActual).toSeconds() * 0.28;

            double higieneActual = mascota.getHigiene() - disminucionHigiene;
            double hambreActual = mascota.getHambre() - disminucionHambre;
            double energiaActual = mascota.getEnergia() - disminucionEnergia;

            double promedioDeEstadisticas = (higieneActual + hambreActual + energiaActual) / 3.0;
            double fluctuacionSalud = ThreadLocalRandom.current().nextDouble(-5.0, 5.0);

            double felicidadActual = this.acotarDecimal(promedioDeEstadisticas); //promedio de higiene, hambre y energia

            double saludActual = this.acotarDecimal(promedioDeEstadisticas + fluctuacionSalud); //promedio + o - una fluctuacion random de 5.0


            mascota.setHigiene(Math.max(higieneActual, 0.0));
            mascota.setHambre(Math.max(hambreActual, 0.0));
            mascota.setEnergia(Math.max(energiaActual, 0.0));
            mascota.setFelicidad(Math.max(felicidadActual, 0.0));
            mascota.setSalud(Math.max(saludActual, 0.0));
            mascota.setEstaEnfermo(this.chequearSiLaMascotaSeEnferma(mascota));

            this.chequearSiLaMascotaSigueViva(mascota);

            this.actualizarMascota(mascota);

            return mascota;
        }

    }

    @Override
    public Boolean chequearSiLaMascotaSeEnferma(MascotaDTO mascota) {

        //Si ya esta enferma, no recorre el metodo para evitar que se cure aleatoriamente.
        if (Boolean.TRUE.equals(mascota.getEstaEnfermo())) {
            return mascota.getEstaEnfermo();
        }

        Double salud = mascota.getSalud();
        Double random = randomProvider.obtenerRandom();
        Boolean seEnferma;

        if (salud < 10.0) {
            seEnferma = true;
        } else if (salud >= 10.0 && salud < 35.0) {
            seEnferma = random < 0.666;
        } else if (salud >= 35.0 && salud < 75.0) {
            seEnferma = random < 0.333;
        } else if (salud >= 75.0 && salud < 95.0) {
            seEnferma = random < 0.050;
        } else {
            seEnferma = false;
        }
        return seEnferma;
    }

    @Override
    public void siHaceFrioYLaMascotaEstaDesabrigadaSePuedeEnfermarConMayorProbabilidad(Clima clima,
            MascotaDTO mascota) {
        Double temperaturaActual = clima.obtenerTemperaturaActual();
        if (temperaturaActual <= Clima.temperaturaFria
                && !(mascota.getEstaAbrigada())) {
            Double saludADisminuir = mascota.getSalud() - 25.0;
            mascota.setSalud(Math.max(0.0, saludADisminuir));
            this.actualizarMascota(mascota);
        }
    }

    @Override
    public MascotaDTO chequearSiLaMascotaSigueViva(MascotaDTO mascota) throws MascotaMuertaException {
        Double random = randomProvider.obtenerRandom();
        Double saludActual = mascota.getSalud();
        Double probabilidadDeMuerte = 0.05 + ((100.0 - saludActual) / 120.0);
        if (!mascota.getEstavivo()) {
            throw new MascotaMuertaException("La mascota ya esta muerta");
        }  else if(!mascota.getEstaEnfermo()){
            return mascota;
        } else {
            if(random < probabilidadDeMuerte) {
                mascota.setEstaVivo(false);
                return mascota;
            } else {
                return mascota;
            }
        }
    }

    private double acotarDecimal(double valor) {
        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        valor = bd.doubleValue();
        return valor;
    }


    @Override
    public MascotaDTO abrigar (MascotaDTO mascota) throws MascotaAbrigadaException {

        if(mascota.getEstaAbrigada()) {
            throw new MascotaAbrigadaException("La mascota ya esta abrigada");
        }

        Double reestablecerSalud = mascota.getSalud() + 25.0;
        mascota.setSalud(Math.min(reestablecerSalud,100.0));
        mascota.setEstaAbrigada(true);
        this.actualizarMascota(mascota);

        return mascota;
    }

    @Override
    public MascotaDTO desabrigar (MascotaDTO mascota) throws MascotaDesabrigadaException {

        if(!mascota.getEstaAbrigada()) {
            throw new MascotaDesabrigadaException("La mascota ya esta desabrigada");
        }
        mascota.setEstaAbrigada(false);
        this.actualizarMascota(mascota);

        return mascota;
    }

    @Override
    public MascotaDTO despertar(MascotaDTO mascota) throws MascotaDespiertaException {
        if(mascota.getEstaDormido()) {
            mascota.setEstaDormido(false);
            mascota.setUltimaSiesta(LocalDateTime.now());
            this.actualizarMascota(mascota);
            return mascota;
        } else {
            throw new MascotaDespiertaException ("La mascota ya esta despierta");
        }
    }

    @Override
    public Boolean chequearSiAlcanzanLasmonedasParaLaAccion(MascotaDTO mascota, Double costo) throws MonedasInsuficientesException {

        if (mascota.getMonedas() >= costo) {
            return true;
        } else {
            throw new MonedasInsuficientesException("Monedas insuficientes, juga para ganar mas!");
        }
    }
}
