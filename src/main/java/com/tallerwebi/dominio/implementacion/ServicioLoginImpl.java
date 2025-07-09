package com.tallerwebi.dominio.implementacion;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

    private RepositorioUsuario repositorioUsuario;
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario consultarUsuario (String email, String password) {
        Boolean usuarioYcontraseniaValidos=false;

        Usuario usuario = this.buscarUsuarioPorEmail(email);

        if (usuario != null) {
            usuarioYcontraseniaValidos = encoder.matches(password, usuario.getPassword());
            //usuarioYcontraseniaValidos=true;
        }
        if(usuarioYcontraseniaValidos) {
            //return repositorioUsuario.buscarUsuario(email, password);
            return usuario;
        }
        return null;
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistente {
        Usuario usuarioEncontrado = repositorioUsuario.buscarUsuario(usuario.getEmail(), usuario.getPassword());
        if(usuarioEncontrado != null){
            throw new UsuarioExistente();
        }
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        repositorioUsuario.guardar(usuario);
    }

    @Override
    public Usuario buscarUsuarioPorEmail(String email) {
        return repositorioUsuario.buscar(email);
    }

}

