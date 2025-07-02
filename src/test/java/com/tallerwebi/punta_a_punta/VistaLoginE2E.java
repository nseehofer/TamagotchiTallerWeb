package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaMascota;
import com.tallerwebi.punta_a_punta.vistas.VistaPresentacion;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
public class VistaLoginE2E {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;
    VistaHome vistaHome;
    VistaMascota vistaMascota;
    VistaPresentacion vistaPresentacion;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(4000));

    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }

    @BeforeEach
    void crearContextoYPagina() {
        ReiniciarDB.limpiarBaseDeDatos();

        context = browser.newContext();
        Page page = context.newPage();

        vistaPresentacion = new VistaPresentacion(page);
        vistaLogin = new VistaLogin(page);
        // PUEDO USAR LA MISMA PAGINA PARA VARIAS VISTAS? 

    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    @Test
    void deberiaDecirUNLAMEnElNavbar() {
        String texto = vistaLogin.obtenerTextoDeLaBarraDeNavegacion();
        assertThat("tamagotchi", equalToIgnoringCase(texto));
    }

    @Test
    void deberiaDarUnErrorAlNoCompletarElLoginYTocarElBoton() {
        vistaLogin.escribirEMAIL("damian@unlam.edu.ar");
        vistaLogin.escribirClave("unlam");
        vistaLogin.darClickEnIniciarSesion();
        String texto = vistaLogin.obtenerMensajeDeError();
        assertThat("Error Usuario o clave incorrecta", equalToIgnoringCase(texto));
    }

    @Test
    void deberiaNavegarAlHomeSiElUsuarioExiste() {
        vistaLogin.irALogin();
        vistaLogin.escribirEMAIL("test@unlam.edu.ar");
        vistaLogin.escribirClave("test");
        vistaLogin.darClickEnIniciarSesion();
        String url = vistaLogin.obtenerURLActual();
        assertThat(url, containsStringIgnoringCase("/spring/home"));
    }

    @Test
    void debeIngresarUsuarioRegistradoYCrearMascota() {
        Page page = context.newPage();
        vistaHome = new VistaHome(page);
        vistaMascota = new VistaMascota(page);
        vistaPresentacion.irAPresentacion();
        vistaPresentacion.darClickEnLogin();
        vistaLogin.irALogin();
        vistaLogin.escribirEMAIL("test@unlam.edu.ar");
        vistaLogin.escribirClave("test");
        vistaLogin.darClickEnIniciarSesion();
        vistaHome.irAHome();
        vistaHome.escribirNombreMascota("Firulero");
        vistaHome.darClickEnCrearMascota();

        String urlMascota = vistaMascota.obtenerURLActual();
        assertThat(urlMascota, containsStringIgnoringCase("/spring/mascota"));
        assertThat(vistaMascota.obtenerNombreMascota(), containsStringIgnoringCase("Firulero"));
    }
}
