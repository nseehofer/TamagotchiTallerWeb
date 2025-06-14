package com.tallerwebi.presentacion;

import java.time.LocalDateTime;

import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;

public class MascotaDTO {

    private Long id;
    private String nombre;
    private Double energia;
    private Double higiene;
    private Double salud;
    private Double felicidad;
    private Double hambre;
    private Boolean estaVivo;
    private LocalDateTime ultimaAlimentacion;
    private LocalDateTime ultimaHigiene;
    private LocalDateTime ultimaSiesta;

    public void setEnergia(Double energia) {
        this.energia = energia;
    }

    public void setHambre(Double hambre) {
        this.hambre = hambre;
    }

    public void setFelicidad(Double felicidad) {
        this.felicidad = felicidad;
    }

    public MascotaDTO(String nombre) {
        this.nombre = nombre;
        this.energia = 100.00;
        this.higiene = 100.00;
        this.salud = 100.00;
        this.felicidad = 100.00;
        this.hambre = 100.00;
        this.estaVivo = true;
        this.ultimaSiesta = LocalDateTime.now();
        this.ultimaHigiene = LocalDateTime.now();
        this.ultimaAlimentacion = LocalDateTime.now();
    }

    public MascotaDTO(Mascota mascota) {
        this.id = mascota.getId();
        this.nombre = mascota.getNombre();
        this.energia = mascota.getEnergia();
        this.higiene = mascota.getHigiene();
        this.salud = mascota.getSalud();
        this.felicidad = mascota.getFelicidad();
        this.hambre = mascota.getHambre();
        this.estaVivo = mascota.getEstaVivo();
        this.ultimaSiesta = mascota.getUltimaSiesta();
        this.ultimaHigiene = mascota.getUltimaHigiene();
        this.ultimaAlimentacion = mascota.getUltimaAlimentacion();
    }

    public Long getId() {
        return this.id;
    };

    public String getNombre() {
        return this.nombre;
    }

    public Double getEnergia() {
        return this.energia;
    }

    public Double getHigiene() {
        return higiene;
    }

    public Double getSalud() {
        return salud;
    }

    public Double getFelicidad() {
        return felicidad;
    }

    public Double getHambre() {
        return hambre;
    }

    public Boolean getEstaVivo() {
        return estaVivo;
    }

    public void setHigiene(Double higiene) {
        this.higiene = higiene;
    }

    public LocalDateTime getUltimaHigiene() {
        return ultimaHigiene;
    }

    public void setUltimaHigiene(LocalDateTime ultimaHigiene) {
        this.ultimaHigiene = ultimaHigiene;
    }

    public Mascota obtenerEntidad() {
        Mascota mascota = new Mascota();
        return this.obtenerEntidad(mascota);
    }

    public Mascota obtenerEntidad(Mascota mascota) {
        if (this.id != null) {
            mascota.setId(this.id);
        }
        mascota.setNombre(this.nombre);
        mascota.setEnergia(this.energia);
        mascota.setHigiene(this.higiene);
        mascota.setSalud(this.salud);
        mascota.setFelicidad(this.felicidad);
        mascota.setHambre(this.hambre);
        mascota.setEstaVivo(this.estaVivo);
        mascota.setUltimaSiesta(this.ultimaSiesta);
        mascota.setUltimaAlimentacion(this.ultimaAlimentacion);
        mascota.setUltimaHigiene(this.ultimaHigiene);
        return mascota;
    }

    public void setUltimaSiesta(LocalDateTime ultimaSiesta) {
        this.ultimaSiesta = ultimaSiesta;
    }

    public void setId(Long idMascota) {
        this.id = idMascota;
    }

    public void setUltimaAlimentacion(LocalDateTime now) {
        this.ultimaAlimentacion = now;
    }

    public LocalDateTime getUltimaAlimentacion() {
        return this.ultimaAlimentacion;
    }

}
