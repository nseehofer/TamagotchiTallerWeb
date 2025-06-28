package com.tallerwebi.infraestructura;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.util.ArrayList;
import java.util.List;

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

import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import com.tallerwebi.presentacion.MascotaDTO;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
@Transactional
public class RepositorioMascotaTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioMascota repositorioMascota;
    private RepositorioUsuarioImpl repositorioUsuario;

    @BeforeEach
    public void init() {
        this.repositorioMascota = new RepositorioMascotaImpl(this.sessionFactory);
        this.repositorioUsuario = new RepositorioUsuarioImpl(this.sessionFactory);
    }

    @Test
    @Rollback
    public void cuandoCreoUnaMascotaConNombreSeGuardaEnLaBaseDeDatos() {

        // GUARDO UN USUARIO PARA ASIGNARLE A LA MASCOTA
        Usuario usuario = obtenerUsuarioParaTest();
        this.repositorioUsuario.guardar(usuario);
        //

        // PRIMERO LA CREO CON EL DTO PARA OTORGARLE UN NOMBRE
        String nombreMascota = "Firulais";
        Mascota mascota = crearMascotaParaTest(usuario, nombreMascota);

        Long idMascotaCreada = this.repositorioMascota.crear(mascota);

        String hql = "FROM Mascota WHERE id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("id", idMascotaCreada);
        Mascota mascotaObtenida = (Mascota) query.getSingleResult();

        assertThat(idMascotaCreada, instanceOf(Long.class));
        assertThat(mascotaObtenida, is(instanceOf(Mascota.class)));
    }

    @Test
    @Rollback
    public void cuandoIngresoElIdDeLaMascotaEntoncesLaObtengo() {

        // GUARDO UN USUARIO PARA ASIGNARLE A LA MASCOTA
        Usuario usuario = obtenerUsuarioParaTest();
        this.repositorioUsuario.guardar(usuario);
        String nombreMascota = "Firulais";
        //

        Mascota mascotaDTOAEntidad = crearMascotaParaTest(usuario,nombreMascota);

        Long idMascotaCreada = this.repositorioMascota.crear(mascotaDTOAEntidad);
        Mascota mascotaObtenida = this.repositorioMascota.obtenerPor(idMascotaCreada);

        //
        String hql = "FROM Mascota WHERE id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("id", idMascotaCreada);
        Mascota mascotaObtenidaPorQueryTest = (Mascota) query.getSingleResult();
        //

        assertThat(mascotaObtenida, instanceOf(Mascota.class));
        assertThat(mascotaObtenidaPorQueryTest, equalTo(mascotaDTOAEntidad));

    }

    @Test
    @Rollback
    public void cuandoIngresoElIdUsuarioObtengoSuListaDeMascotas() {
        Usuario usuario = obtenerUsuarioParaTest();
        this.repositorioUsuario.guardar(usuario);
        String nombreMascotaUno = "Firulais";
        String nombreMascotaDos = "Toby";

        Mascota mascotaUno = crearMascotaParaTest(usuario, nombreMascotaUno);
        mascotaUno.setEstaVivo(false);
        crearMascotaParaTest(usuario, nombreMascotaDos);

        List <Mascota> mascotasEsperadas = this.repositorioMascota.obtenerListaDeMascotasDeUnUsuario(usuario.getId());

        String hql = "FROM Mascota WHERE usuario_id = :usuario_id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("usuario_id", usuario.getId());
        List<Mascota> mascotasObtenidas = (List<Mascota>) query.getResultList();

        assertThat(mascotasObtenidas, equalTo(mascotasEsperadas));

    }

    @Test
    @Rollback
    public void cuandoIngresoUnaMascotaActualizadaEntoncesActualizaLosDatosQueModifique() {
        Usuario usuario = obtenerUsuarioParaTest();
        this.repositorioUsuario.guardar(usuario);
        String nombreMascota = "Firulais";

        Mascota mascota = crearMascotaParaTest(usuario, nombreMascota);

        this.repositorioMascota.crear(mascota);
        String nombreActualizado = "Firulero";
        mascota.setNombre(nombreActualizado);

        this.repositorioMascota.actualizar(mascota);

        String hql = "SELECT nombre FROM Mascota WHERE usuario_id = :usuario_id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("usuario_id", usuario.getId());
        String nombreMascotasObtenida = (String) query.getSingleResult();

        assertThat(nombreMascotasObtenida, equalTo(nombreActualizado));
    }

    @Test
    @Rollback
    public void cuandoPidoListadoDeMascotaEntoncesLoObtengo() {
        Usuario usuario = obtenerUsuarioParaTest();
        this.repositorioUsuario.guardar(usuario);
        String nombreMascotaUno = "Firulais";
        String nombreMascotaDos = "Toby";

        Mascota mascotaUno = crearMascotaParaTest(usuario, nombreMascotaUno);
        mascotaUno.setEstaVivo(false);
        crearMascotaParaTest(usuario, nombreMascotaDos);

        List <Mascota> mascotasEsperadas = this.repositorioMascota.obtenerListaDeMascotas();

        String hql = "FROM Mascota";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        List<Mascota> mascotasObtenidas = (List<Mascota>) query.getResultList();

        assertThat(mascotasObtenidas, equalTo(mascotasEsperadas));

    }


    // METODOS PRIVADOS PARA REUTILIZAR EN LOS TESTS
    private Usuario obtenerUsuarioParaTest() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carla");
        usuario.setEmail("email@gmail.com");
        usuario.setRol("USER");
        usuario.setPassword("123456789");
        usuario.setActivo(true);
        usuario.setPais("Alemania");
        usuario.setProvincia("Abenberg");
        return usuario;
    }

    private Mascota crearMascotaParaTest(Usuario usuario, String nombreMascota) {
        MascotaDTO mascota = new MascotaDTO(nombreMascota);
        mascota.setIdUsuario(usuario.getId());
        Mascota mascotaDTOAEntidad = mascota.obtenerEntidad();
        return mascotaDTOAEntidad;
    }

}
