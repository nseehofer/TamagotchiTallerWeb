package com.tallerwebi.dominio.entidades;

import com.tallerwebi.dominio.Usuario;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity

public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;
    @Column
    private Double energia;
    @Column
    private Double higiene;
    @Column
    private Double salud;
    @Column
    private Double felicidad;
    @Column
    private Double hambre;
    @Column
    private Boolean estaVivo;
    @Column
    private LocalDateTime ultimaSiesta;

    @Column
    private LocalDateTime ultimaAlimentacion;

    @Column
    private LocalDateTime ultimaHigiene;

    @ManyToOne
    Usuario usuario;

    public Mascota(){};
    public Mascota(Long id, String nombre, Double energia, Double higiene,Double salud,Double felicidad,Double hambre,Boolean estaVivo) {
        this.id = id;
        this.nombre = nombre;
        this.energia = energia;
        this.higiene=higiene;
        this.salud=salud;
        this.felicidad=felicidad;
        this.hambre= hambre;
        this.estaVivo=estaVivo;
        this.ultimaAlimentacion = LocalDateTime.now();
        this.ultimaHigiene = LocalDateTime.now();
        this.ultimaSiesta = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Double getEnergia() {
        return energia;
    }

    public void setEnergia(Double energia) {
        this.energia = energia;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getHigiene() {
        return higiene;
    }

    public void setHigiene(Double higiene) {
        this.higiene = higiene;
    }

    public Double getSalud() {
        return salud;
    }

    public void setSalud(Double salud) {
        this.salud = salud;
    }

    public Double getFelicidad() {
        return felicidad;
    }

    public void setFelicidad(Double felicidad) {
        this.felicidad = felicidad;
    }

    public Double getHambre() {
        return hambre;
    }

    public void setHambre(Double hambre) {
        this.hambre = hambre;
    }

    public Boolean getEstaVivo() {
        return estaVivo;
    }

    public void setEstaVivo(Boolean estaVivo) {
        this.estaVivo = estaVivo;
    }

    public void setUltimaSiesta(LocalDateTime ultimaSiesta) {this.ultimaSiesta = ultimaSiesta; }

    public LocalDateTime getUltimaSiesta() {return this.ultimaSiesta;}

    public void setUltimaAlimentacion(LocalDateTime now) {
        this.ultimaAlimentacion = now;
    }

    public LocalDateTime getUltimaAlimentacion() {
        return ultimaAlimentacion;
    }

    public LocalDateTime getUltimaHigiene() {
        return ultimaHigiene;
    }

    public void setUltimaHigiene(LocalDateTime ultimaHigiene) {
        this.ultimaHigiene = ultimaHigiene;
    }
}
