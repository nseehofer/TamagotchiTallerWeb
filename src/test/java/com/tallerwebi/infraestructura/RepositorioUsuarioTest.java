package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
@Transactional
public class RepositorioUsuarioTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioUsuarioImpl repositorioUsuario;

    @BeforeEach
    public void init() {
        this.repositorioUsuario = new RepositorioUsuarioImpl(this.sessionFactory);
    }

    @Test
    @Rollback
    public void cuandoSeCreaUnUsuarioEntoncesSeCargaEnLaBaseDeDatos() {
        String emailParaUsuario = "emailcualquiera@gmail.com";
        Usuario usuarioEsperado = crearUsuarioParaTest(emailParaUsuario);

        this.repositorioUsuario.guardar(usuarioEsperado);

        String hql = "FROM Usuario WHERE id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("id", usuarioEsperado.getId());
        Usuario usuarioObtenido = (Usuario) query.getSingleResult();

    
        assertThat(usuarioEsperado, equalTo(usuarioObtenido));

    }

    @Test
    @Rollback
    public void cuandoBuscaUnUsuarioPorEmailYPasswordEntoncesLoObtiene(){
        String emailParaUsuario = "emailcualquiera@gmail.com";
        Usuario usuario = crearUsuarioParaTest(emailParaUsuario);
        this.repositorioUsuario.guardar(usuario);

        Usuario usuarioEsperado = this.repositorioUsuario.buscarUsuario(emailParaUsuario, usuario.getPassword());

        String hql = "FROM Usuario WHERE id = :id AND email = :email";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("id", usuario.getId());
        query.setParameter("email", emailParaUsuario);
        Usuario usuarioObtenido = (Usuario) query.getSingleResult();

        assertThat(usuarioEsperado, equalTo(usuarioObtenido));


    }

    @Test
    @Rollback
    public void cuandoBuscaUnUsuarioConSuEmailEntoncesLoObtiene(){
        String emailParaUsuario = "emailcualquiera@gmail.com";
        Usuario usuario = crearUsuarioParaTest(emailParaUsuario);
        this.repositorioUsuario.guardar(usuario);

        Usuario usuarioEsperado = this.repositorioUsuario.buscar(emailParaUsuario);

        String hql = "FROM Usuario WHERE email = :email";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("email", emailParaUsuario);
        Usuario usuarioObtenido = (Usuario) query.getSingleResult();

        assertThat(usuarioEsperado, equalTo(usuarioObtenido));

    }

    @Test
    @Rollback
    public void cuandoModificaUnUsuarioEntoncesSeModificanLosDatosEnLaBase() {
        String emailParaUsuario = "emailcualquiera@gmail.com";
        Usuario usuario = crearUsuarioParaTest(emailParaUsuario);
        this.repositorioUsuario.guardar(usuario);
        usuario.setNombre("JuanDeLaCalle");

        this.repositorioUsuario.modificar(usuario);

        String hql = "FROM Usuario WHERE id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("id", usuario.getId());
        Usuario usuarioObtenido = (Usuario) query.getSingleResult();

        String nombreEsperado = usuario.getNombre();
        String nombreObtenido = usuarioObtenido.getNombre();

        assertThat(nombreEsperado, equalTo(nombreObtenido));

    }

    private Usuario crearUsuarioParaTest(String email) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail(email);
        usuario.setPassword("123456789");
        usuario.setActivo(true);
        usuario.setRol("USER");
        usuario.setPais("Bolivia");
        usuario.setProvincia("Sucre");
        return usuario;
    }
}
