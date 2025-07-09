package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaPresentacion extends VistaWeb {

    public VistaPresentacion(Page page) {
        super(page);
        page.navigate("localhost:8080/spring/");
    }

    public String obtenerURLActual(){
        return page.url();
    }

    public void darClickEnLogin(){
        this.darClickEnElElemento("#btn-login");
    }

    public void irAPresentacion() {
    page.navigate("http://localhost:8080/spring/");
}
}
