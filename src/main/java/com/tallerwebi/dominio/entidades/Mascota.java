package com.tallerwebi.dominio.entidades;

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

    private Long usuario_id;
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", insertable = false, updatable=false)
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

    public Long getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(Long usuario_id) {
        this.usuario_id = usuario_id;
    }

    // SOBREESCIBO HASHCODE & EQUALS TENIENDO EN CUENTA, ID, NOMBE, ID_USUARIO 
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        result = prime * result + ((usuario_id == null) ? 0 : usuario_id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Mascota other = (Mascota) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        if (usuario_id == null) {
            if (other.usuario_id != null)
                return false;
        } else if (!usuario_id.equals(other.usuario_id))
            return false;
        return true;
    }

    
    
    

}
