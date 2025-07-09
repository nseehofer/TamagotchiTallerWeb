package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaMascota extends VistaWeb {

    public VistaMascota(Page page) {
        super(page);
        page.navigate("localhost:8080/spring/mascota");
    }

    public String obtenerURLActual(){
        return page.url();
    }

    public String obtenerNombreMascota() {
        return this.obtenerTextoDelElemento("#nombre-mascota");
    }

    public String obtenerEnergiaMascota() {
        return this.obtenerTextoDelElemento("#valor-energia");
    }

    public void darClickEnJugar(){
        this.darClickEnElElemento("#jugar");
    }
}
