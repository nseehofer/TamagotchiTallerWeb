package com.tallerwebi.integracion;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioLogin;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.implementacion.ServicioLoginImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.*;

public class ServicioLoginTest {

    private RepositorioUsuario repositorioUsuario;
    private ServicioLogin servicioLogin;
    private Usuario usuarioMock;
    private PasswordEncoder encoder;

    @BeforeEach
    public void inicializar() {
        this.repositorioUsuario = mock(RepositorioUsuario.class);
        this.servicioLogin = new ServicioLoginImpl(this.repositorioUsuario);
        usuarioMock = mock(Usuario.class);
        encoder =  new BCryptPasswordEncoder();
    }


    @Test
    public void consultarUsuarioDevuelveUnUsuario(){
        when(servicioLogin.buscarUsuarioPorEmail("a@a")).thenReturn(usuarioMock);
        when(usuarioMock.getPassword()).thenReturn(encoder.encode("123"));

        Usuario usuarioObtenido = servicioLogin.consultarUsuario("a@a", "123");

        assertThat(usuarioObtenido, is(instanceOf(Usuario.class)));
    }

    @Test
    public void registrarLlamaAGuardarUsuarioSiElUsuarioNoExiste() throws UsuarioExistente {
        when(repositorioUsuario.buscarUsuario(null, null)).thenReturn(null);
        when(usuarioMock.getPassword()).thenReturn("null");
        servicioLogin.registrar(usuarioMock);

        verify(repositorioUsuario).buscarUsuario(usuarioMock.getEmail(),usuarioMock.getPassword());
        verify(repositorioUsuario).guardar(usuarioMock);
    }

    @Test
    public void registrarLanzaUnaExcepcionSiElUsuarioYaExisteYNoInvocaAlMetodoGuardar() throws UsuarioExistente {
        when(repositorioUsuario.buscarUsuario(null, null)).thenReturn(usuarioMock);

        UsuarioExistente exception = Assertions.assertThrows(UsuarioExistente.class, () -> {
            servicioLogin.registrar(usuarioMock);
        });

        verify(repositorioUsuario,times(0)).guardar(usuarioMock);

    }

    @Test
    public void buscarUsuarioPorEmailDevuelveUnUsuario() {
        when(repositorioUsuario.buscar("a@a")).thenReturn(usuarioMock);

        Usuario usuarioObtenido = servicioLogin.buscarUsuarioPorEmail("a@a");

        assertThat(usuarioObtenido, is(instanceOf(Usuario.class)));
    }
}
