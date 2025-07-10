package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaSeleccionarTipoMascota extends VistaWeb {

    public VistaSeleccionarTipoMascota(Page page) {
        super(page);
        page.navigate("localhost:8080/spring/mascota/crearconpost");
    }

    public String obtenerURLActual() {
        return page.url();
    }

    public void darClickEnSeleccionarTipoMascotaPerro(){
        this.darClickEnElElemento("#option-perro");
    }

    public void darClickEnCrearMascotaConTipo(){
        this.darClickEnElElemento("#crear-mascota-tipo");
    }

    public void irASeleccionarTipo() {
        page.navigate("http://localhost:8080/spring/mascota/crearconpost");
    }

}
