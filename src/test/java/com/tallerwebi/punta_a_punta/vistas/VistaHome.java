package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaHome extends VistaWeb{

     public VistaHome(Page page) {
        super(page);
        page.navigate("localhost:8080/spring/home");
    }

    public String obtenerURLActual(){
        return page.url();
    }

     public void escribirNombreMascota(String nombreMascota){
        this.escribirEnElElemento("#nombre-mascota", nombreMascota);
    }

    public void darClickEnCrearMascota(){
        this.darClickEnElElemento("#btn-crear-mascota");
    }

    public void irAHome() {
    page.navigate("http://localhost:8080/spring/home");
}
}
