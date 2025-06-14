package com.tallerwebi.dominio.excepcion;

public class EnergiaInsuficiente extends RuntimeException {
    public EnergiaInsuficiente(String message) {
        super(message);
    }
}
