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
    private Boolean estaEnfermo;
    private LocalDateTime ultimaAlimentacion;
    private LocalDateTime ultimaHigiene;
    private LocalDateTime ultimaSiesta;
    private Long idUsuario;
    private Boolean estaAbrigada;
    private Boolean estaDormido;
    private Boolean estaJugando;

    private String tipo;
    private Double monedas;

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
        this.estaEnfermo = false;
        this.ultimaSiesta = LocalDateTime.now();
        this.ultimaHigiene = LocalDateTime.now();
        this.ultimaAlimentacion = LocalDateTime.now();
        this.estaAbrigada = false;
        this.tipo = "desconocido";
        this.estaDormido = false;
        this.estaJugando = false;

    }

    public MascotaDTO(String nombre, Long idUsuario) {
        this.nombre = nombre;
        this.energia = 100.00;
        this.higiene = 100.00;
        this.salud = 100.00;
        this.felicidad = 100.00;
        this.hambre = 100.00;
        this.estaVivo = true;
        this.estaEnfermo = false;
        this.ultimaSiesta = LocalDateTime.now();
        this.ultimaHigiene = LocalDateTime.now();
        this.ultimaAlimentacion = LocalDateTime.now();
        this.estaDormido = false;
        this.idUsuario = idUsuario;
        this.estaAbrigada = false;
        this.tipo = null;
        this.estaJugando = false;
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
        this.estaEnfermo = mascota.getEstaEnfermo();
        this.ultimaSiesta = mascota.getUltimaSiesta();
        this.ultimaHigiene = mascota.getUltimaHigiene();
        this.ultimaAlimentacion = mascota.getUltimaAlimentacion();
        this.estaDormido = mascota.getEstaDormido();
        this.estaAbrigada = mascota.getEstaAbrigada();
        this.tipo = mascota.getTipo();
        this.estaJugando = mascota.getEstaJugando();
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

    public LocalDateTime getUltimaSiesta() {
        return ultimaSiesta;
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
        mascota.setEnergia(redondearDosDecimales(this.energia));
        mascota.setHigiene(redondearDosDecimales(this.higiene));
        mascota.setSalud(redondearDosDecimales(this.salud));
        mascota.setFelicidad(redondearDosDecimales(this.felicidad));
        mascota.setHambre(redondearDosDecimales(this.hambre));
        mascota.setEstaVivo(this.estaVivo);
        mascota.setEstaEnfermo(this.estaEnfermo);
        mascota.setUltimaSiesta(this.ultimaSiesta);
        mascota.setUltimaAlimentacion(this.ultimaAlimentacion);
        mascota.setUltimaHigiene(this.ultimaHigiene);
        mascota.setUsuario_id(this.idUsuario);
        mascota.setEstaAbrigada(this.estaAbrigada);
        mascota.setTipo(this.tipo);
        mascota.setEstaDormido(this.estaDormido);
        mascota.setEstaJugando(this.estaJugando);
        return mascota;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public Boolean getEstaEnfermo() {
        return estaEnfermo;
    }

    public void setEstaEnfermo(Boolean estaEnfermo) {
        this.estaEnfermo = estaEnfermo;
    }

    public void setSalud(Double salud) {
        this.salud = salud;
    }

    public Boolean getEstaAbrigada() {
        return estaAbrigada;
    }

    public void setEstaAbrigada(Boolean estaAbrigada) {
        this.estaAbrigada = estaAbrigada;
    }

    public boolean getEstavivo() {
        return this.estaVivo;
    }

    public void setEstaVivo(boolean estaVivo) {
        this.estaVivo = estaVivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEstaVivo(Boolean estaVivo) {
        this.estaVivo = estaVivo;
    }

    public Boolean getEstaDormido() {
        return estaDormido;
    }

    public void setEstaDormido(Boolean estaDormido) {
        this.estaDormido = estaDormido;
    }

    public Boolean getEstaJugando() {
        return estaJugando;
    }

    public void setEstaJugando(Boolean estaJugando) {
        this.estaJugando = estaJugando;
    }

    public void setMonedas(Double monedas) {
        this.monedas = monedas;
    }

    public Double getMonedas() {
        return monedas;
    }

    private Double redondearDosDecimales(Double valor) {
        return valor != null ? Math.round(valor * 100.0) / 100.0 : null;
    }

}
