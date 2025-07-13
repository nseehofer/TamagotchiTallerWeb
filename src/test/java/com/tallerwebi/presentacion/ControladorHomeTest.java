package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControladorHomeTest {

    private ControladorHome controladorHome;
    private ServicioMascota servicioMascotaMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private ServicioLogin servicioLoginMock;

    @BeforeEach
    public void init(){
        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        servicioMascotaMock = mock(ServicioMascota.class);
        servicioLoginMock = mock(ServicioLogin.class);
        controladorHome = new ControladorHome(servicioMascotaMock,servicioLoginMock);
    }

    @Test
    public void queSeDevuelvaLaVistaHomeCuandoHayUnUsuarioLogueado(){
        List<Mascota> mascotas = mock();
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getSession().getAttribute("ID")).thenReturn(1L);
        when(requestMock.getSession().getAttribute("NOMBRE")).thenReturn("Juan");

        when(servicioMascotaMock.traerMascotasDeUnUsuario(1L)).thenReturn(mascotas);

        ModelAndView modelAndView = controladorHome.irAHome(requestMock);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("home"));
        assertThat(modelAndView.getModel().get("idUsuario").toString(), equalToIgnoringCase("1"));
        assertThat(modelAndView.getModel().get("nombreUsuario").toString(), equalToIgnoringCase("Juan"));
        assertThat(modelAndView.getModel().get("mascotas"), notNullValue());

    }

    @Test
    public void queDevuelvaLaVistaLoginCuandoNOHayUnUsuarioLogueado(){

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getSession().getAttribute("ID")).thenReturn(null);

        ModelAndView modelAndView = controladorHome.irAHome(requestMock);

        assertThat("redirect:/login", equalToIgnoringCase(Objects.requireNonNull(modelAndView.getViewName())));
    }
}
