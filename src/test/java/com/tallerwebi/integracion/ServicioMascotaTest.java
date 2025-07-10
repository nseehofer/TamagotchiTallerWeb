package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RandomProvider;
import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.implementacion.ServicioMascotaImp;
import com.tallerwebi.presentacion.MascotaDTO;
import javassist.LoaderClassPath;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioMascotaTest {

    private RepositorioMascota repositorioMascota;
    private ServicioMascota servicioMascota;
    private RandomProvider randomProvider;

    @BeforeEach
    public void inicializar() {
        this.repositorioMascota = mock(RepositorioMascota.class);
        this.randomProvider = mock(RandomProvider.class);
        this.servicioMascota = new ServicioMascotaImp(this.repositorioMascota, this.randomProvider);
    }

    @Test
    public void cuandoLimpioLaMascotaSuHigieneSeAumentaAlMaximo() throws LimpiezaMaximaException {
        //Preparación
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(20.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setEnergia(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        //Accion
        servicioMascota.limpiarMascota(mascotaDTO);

        //Verificacion
        assertThat(mascotaDTO.getHigiene(), equalTo(100.0));
    }

    @Test
    public void cuandoSeLimpiaUnaMascotaConHigieneMaximaLanzaUnaExcepcion() throws LimpiezaMaximaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        try {
            servicioMascota.limpiarMascota(mascotaDTO);
            fail ("Se esperaba LimpiezaMaximaException, pero no se lanzó");
        } catch (LimpiezaMaximaException e) {
            assertThat(e.getMessage(), containsString("La higiene ya se encuentra al máximo"));
        }

    }

    @Test
    public void queLasEstadisticasBajenProgresivamenteConElTiempo() throws MascotaMuertaException, MascotaDespiertaException {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setEstaVivo(true);
        mascotaEntidad.setEstaDormido(false);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        //ACCION
        LocalDateTime ultimaHoraHigiene = LocalDateTime.now().minusHours(3);
        LocalDateTime ultimaHoraAlimentacion = LocalDateTime.now().minusHours(3);
        LocalDateTime ultimaHoraSiesta = LocalDateTime.now().minusHours(3);


        LocalDateTime horaActual = LocalDateTime.now();

        mascotaDTO.setUltimaHigiene(ultimaHoraHigiene);
        mascotaDTO.setUltimaAlimentacion(ultimaHoraAlimentacion);
        mascotaDTO.setUltimaSiesta(ultimaHoraSiesta);

        servicioMascota.actualizarEstadisticas(mascotaDTO, horaActual);

        //VERIFICACION
        assertThat(mascotaDTO.getHigiene() < 100.0, equalTo(true));
        assertThat(mascotaDTO.getEnergia() < 100.0, equalTo(true));
        assertThat(mascotaDTO.getHambre() < 100.0, equalTo(true));
        assertThat(mascotaDTO.getFelicidad() < 100.0, equalTo(true));

    }

    @Test
    public void queAlPasarUnTiempoExcesivamenteLargoLasEstadisticasNoDisminuyenMenosQueCero() throws MascotaMuertaException, MascotaDespiertaException {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setSalud(100.0);
        mascotaEntidad.setEstaDormido(false);
        mascotaEntidad.setEstaVivo(true);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        //ACCION
        LocalDateTime ultimaHoraHigiene = LocalDateTime.now().minusDays(10);
        LocalDateTime ultimaHoraAlimentacion = LocalDateTime.now().minusDays(10);
        LocalDateTime ultimaHoraSiesta = LocalDateTime.now().minusDays(10);

        LocalDateTime horaActual = LocalDateTime.now();


        mascotaDTO.setUltimaHigiene(ultimaHoraHigiene);
        mascotaDTO.setUltimaAlimentacion(ultimaHoraAlimentacion);
        mascotaDTO.setUltimaSiesta(ultimaHoraSiesta);

        servicioMascota.actualizarEstadisticas(mascotaDTO, horaActual);

        //VERIFICACION
        assertThat(mascotaDTO.getHigiene() == 0.0, equalTo(true));
        assertThat(mascotaDTO.getHambre() == 0.0, equalTo(true));
        assertThat(mascotaDTO.getEnergia() == 0.0, equalTo(true));
        assertThat(mascotaDTO.getFelicidad() == 0.0, equalTo(true));
        assertThat(mascotaDTO.getSalud() == 0.0, equalTo(true));

    }

    @Test
    public void queAlJugarDisminuye25PuntosDeHigiene() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setHambre(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.jugar(mascotaDTO);

        //VERIFICACION
        assertThat(mascotaDTO.getHigiene(), equalTo(75.0));
    }


    @Test
    public void cuandoLaMascotaSeAumentaEnergiaConElPasoDelTiempo() throws EnergiaMaxima, MascotaMuertaException, MascotaDespiertaException {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(60.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setSalud(100.0);
        mascotaEntidad.setUltimaSiesta(LocalDateTime.now());
        mascotaEntidad.setUltimaHigiene(LocalDateTime.now());
        mascotaEntidad.setUltimaAlimentacion(LocalDateTime.now());

        LocalDateTime horaAComparar = LocalDateTime.now().plusMinutes(1);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());
        servicioMascota.dormir(mascotaDTO);
        servicioMascota.actualizarEstadisticas(mascotaDTO, horaAComparar);

        //VERIFICACION
        assertThat(mascotaDTO.getEnergia(), greaterThan(60.0));
    }

    @Test
    public void cuandoSeDuermeUnaMascotaConEnergiaMaximaLanzaUnaExcepcion() throws EnergiaMaxima {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        //VERIFICACION
        try {
            servicioMascota.dormir(mascotaDTO);
            fail ("Se esperaba EnergiaMaxima, pero no se lanzó");
        } catch (EnergiaMaxima e) {
            assertThat(e.getMessage(), containsString("No se puede dormir porque no tiene sueño"));
        }

    }

    @Test
    public void queAlDormirLaEnergiaNoSubeDe100() throws EnergiaMaxima, MascotaMuertaException, MascotaDespiertaException {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(60.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setSalud(100.0);
        mascotaEntidad.setUltimaSiesta(LocalDateTime.now());
        mascotaEntidad.setUltimaHigiene(LocalDateTime.now());
        mascotaEntidad.setUltimaAlimentacion(LocalDateTime.now());

        LocalDateTime horaAComparar = LocalDateTime.now().plusDays(1);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());
        servicioMascota.dormir(mascotaDTO);
        servicioMascota.actualizarEstadisticas(mascotaDTO, horaAComparar);

        //VERIFICACION
        assertThat(mascotaDTO.getEnergia(), equalTo(100.0));
    }


    @Test
    public void queAlJugarDisminuye25PuntosDeEnergia() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setHambre(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.jugar(mascotaDTO);

        //VERIFICACION
        assertThat(mascotaDTO.getEnergia(), equalTo(75.0));
    }

    @Test
    public void queLaMascotaSeEnfermeSiempreCuandoLasEstadisticasBajanAlMinimo () {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(0.0);
        mascotaEntidad.setEnergia(0.0);
        mascotaEntidad.setFelicidad(0.0);
        mascotaEntidad.setHambre(0.0);
        mascotaEntidad.setSalud(0.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultadoObtenido = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultadoObtenido, equalTo(true));

    }

    @Test
    public void queSePuedaCrearUnaMascotaConNombre() {

        MascotaDTO mascotaCreada = servicioMascota.crearMascota("firu");

        assertThat(mascotaCreada, notNullValue());
        assertThat(mascotaCreada.getNombre(), equalTo("firu"));

    }

    @Test
    public void queSePuedaCrearUnaMascotaConNombreYUsuario(){

        MascotaDTO mascotaCreada = servicioMascota.crearMascota("firu", 1L);

        assertThat(mascotaCreada, notNullValue());
        assertThat(mascotaCreada.getNombre(), equalTo("firu"));
        assertThat(mascotaCreada.getIdUsuario(), equalTo(1L));
    }

    @Test
    public void queAlJugarConEnergiaInsuficienteLanceEnergiaInsuficienteException(){
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setEnergia(0.0);
        mascotaEntidad.setFelicidad(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        try {
            servicioMascota.jugar(mascotaDTO);
        } catch (EnergiaInsuficiente e) {
            assertThat(e.getMessage(), equalTo("No podés jugar, te falta energía"));
        }

    }

    @Test
    public void queAlAlimentarseSeAumenteElNivelDeHambreYSeGuardeHoraDeUltimaAlimentacion() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHambre(50.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setHigiene(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.alimentar(mascotaDTO);

        assertThat(mascotaDTO.getHambre(), equalTo(75.0));
        assertThat(mascotaDTO.getUltimaAlimentacion(), notNullValue());

    }

    @Test
    public void queAlIntentarAlimentarConHambreAlMaximoLanceMascotaSatisfechaException() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHambre(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        try {
            servicioMascota.alimentar(mascotaDTO);
        } catch (MascotaSatisfecha e){
            assertThat(e.getMessage(), equalTo("Tu mascota está satisfecha"));
        }
    }

    @Test
    public void queUnaMascotaQueYaEstaEnfermaNoChequeeSiSePuedeEnfermar() {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEstaEnfermo(true);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(mascotaDTO.getEstaEnfermo(), equalTo(true));
    }

    @Test
    public void queSePuedaCrearUnaMascotaYPersista() {
        // MascotaDTO que quiero crear
        MascotaDTO mascotaDTO = new MascotaDTO("firulais");

        // Mascota que simula lo que devuelve el repositorio (ya con ID)
        Mascota mascotaGuardada = mascotaDTO.obtenerEntidad();
        mascotaGuardada.setId(1L);

        // Configurar mocks
        when(repositorioMascota.crear(any())).thenReturn(1L);
        when(repositorioMascota.obtenerPor(1L)).thenReturn(mascotaGuardada);

        // Ejecutar
        MascotaDTO resultado = servicioMascota.crear(mascotaDTO);

        // Verificar
        assertThat(resultado.getId(), equalTo(1L));
        assertThat(resultado.getNombre(), equalTo("firulais"));
    }

    @Test
    public void queSePuedaObtenerUnaListaDeTodasLasMascotas() {
        Mascota mascota1 = new Mascota();
        mascota1.setId(1L);
        mascota1.setNombre("Firu");

        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        mascota2.setNombre("Luna");

        List<Mascota> listaMascota = new ArrayList <>();

        listaMascota.add(mascota1);
        listaMascota.add(mascota2);

        when(repositorioMascota.obtenerListaDeMascotas()).thenReturn(listaMascota);

        List <Mascota> resultado = servicioMascota.traerMascotas();

        assertThat(resultado.size(), equalTo(2));
        assertThat(resultado.get(0).getNombre(), equalTo("Firu"));
        assertThat(resultado.get(1).getNombre(), equalTo("Luna"));


    }

    @Test
    public void queSePuedanOBtenerLaListaDeMascotasDeUnUsuario() {
        Mascota mascota1 = new Mascota();
        mascota1.setId(1L);
        mascota1.setNombre("Firu");

        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        mascota2.setNombre("Luna");

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        List<Mascota> listaMascotasUsuario = new ArrayList <>();
        listaMascotasUsuario.add(mascota1);
        listaMascotasUsuario.add(mascota2);

        when(repositorioMascota.obtenerListaDeMascotasDeUnUsuario(usuario.getId())).thenReturn(listaMascotasUsuario);

        List <Mascota> resultado = servicioMascota.traerMascotasDeUnUsuario(usuario.getId());

        assertThat(resultado.size(), equalTo(2));
        assertThat(resultado.get(0).getNombre(), equalTo("Firu"));
        assertThat(resultado.get(1).getNombre(), equalTo("Luna"));
    }

   @Test
    public void queLaMascotaSeEnfermeSiTiene30DeSalud() {


        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(30.0);


        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.0);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(true));
    }

    @Test
    public void queLaMascotaSeEnfermeSiTiene50DeSalud() {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(50.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.0);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(true));
    }


    @Test
    public void queLaMascotaSeEnfermeSiTiene80DeSalud() {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(80.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.0);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(true));
    }

    @Test
    public void queLaMascotaNoSeEnfermeSiTiene100DeSalud() {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.0);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(false));
    }

    @Test
    public void queLaMascotaNoSeEnfermeSiTiene30DeSalud() {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(30.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.999);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(false));
    }

    @Test
    public void queLaMascotaNoSeEnfermeSiTiene50DeSalud() {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(50.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.999);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(false));
    }

    @Test
    public void queLaMascotaNoSeEnfermeSiTiene80DeSalud() {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(80.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.999);

        MascotaDTO mascotaDTO  = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        Boolean resultado = servicioMascota.chequearSiLaMascotaSeEnferma(mascotaDTO);

        assertThat(resultado, equalTo(false));
    }

    @Test
    public void queSePuedaCurarUnaMascotaEnferma() throws MascotaSanaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaEnfermo(true);
        mascotaEntidad.setId(1L);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.curarMascota(mascotaDTO);

        assertThat(mascotaDTO.getEstaEnfermo(), equalTo(false));
    }

    @Test
    public void queNoSePuedaCurarUnaMascotaQueNoEstaEnferma() throws MascotaSanaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaEnfermo(false);
        mascotaEntidad.setId(1L);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        try {
            servicioMascota.curarMascota(mascotaDTO);
        } catch (MascotaSanaException e) {
            assertThat(e.getMessage(), equalTo("La mascota no esta enferma"));
        }

    }

    @Test
    public void queLaMascotaSeDespierteCuandoSuEnergiaLlegaACien() throws MascotaMuertaException, MascotaDespiertaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(60.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setSalud(100.0);
        mascotaEntidad.setUltimaSiesta(LocalDateTime.now());
        mascotaEntidad.setUltimaHigiene(LocalDateTime.now());
        mascotaEntidad.setUltimaAlimentacion(LocalDateTime.now());
        mascotaEntidad.setEstaDormido(true);

        LocalDateTime horaAComparar = LocalDateTime.now().plusDays(1);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());
        servicioMascota.actualizarEstadisticas(mascotaDTO, horaAComparar);

        //VERIFICACION
        assertThat(mascotaDTO.getEstaDormido(), equalTo(false));
    }

    @Test
    public void queNoSePuedaDespertarUnaMascotaQueYaEstaDespierta(){
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaDormido(false);
        mascotaEntidad.setId(1L);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        try {
            servicioMascota.despertar(mascotaDTO);
        } catch (MascotaDespiertaException e) {
            assertThat(e.getMessage(), equalTo("La mascota ya esta despierta"));
        }
    }

    @Test
    public void queNoSePuedaMorirUnaMascotaQueYaEstaMuerta() {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaVivo(false);
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(75.0);


        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        try {
            servicioMascota.chequearSiLaMascotaSigueViva(mascotaDTO);
        } catch (MascotaMuertaException e) {
            assertThat(e.getMessage(), equalTo("La mascota ya esta muerta"));
        }

    }

    @Test
    public void queUnaMascotaQueNoEstaEnfermaNoChequeeSiSePuedeMorir() throws MascotaMuertaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaVivo(true);
        mascotaEntidad.setEstaEnfermo(false);
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(75.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.chequearSiLaMascotaSigueViva(mascotaDTO);

        assertThat(mascotaDTO.getEstaEnfermo(), equalTo(false));
        assertThat(mascotaDTO.getEstaVivo(), equalTo(true));
    }

    @Test
    public void queUnaMascotaEnfermaSeMueraCuandoNoPasaElChequeo() throws MascotaMuertaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaVivo(true);
        mascotaEntidad.setEstaEnfermo(true);
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(50.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.0);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.chequearSiLaMascotaSigueViva(mascotaDTO);

        assertThat(mascotaDTO.getEstavivo(), equalTo(false));

    }

    @Test
    public void queUnaMascotaEnfermaNoSeMueraCuandoPasaElChequeo() throws MascotaMuertaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setEstaVivo(true);
        mascotaEntidad.setEstaEnfermo(true);
        mascotaEntidad.setId(1L);
        mascotaEntidad.setSalud(50.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);
        when(randomProvider.obtenerRandom()).thenReturn(0.999);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.chequearSiLaMascotaSigueViva(mascotaDTO);

        assertThat(mascotaDTO.getEstavivo(), equalTo(true));

    }

    @Test
    public void queLaMascotaNoSeDespierteSiLaEnergiaNoLlegoACien() throws MascotaMuertaException, MascotaDespiertaException {
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(20.0);
        mascotaEntidad.setFelicidad(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setSalud(100.0);
        mascotaEntidad.setUltimaSiesta(LocalDateTime.now());
        mascotaEntidad.setUltimaHigiene(LocalDateTime.now());
        mascotaEntidad.setUltimaAlimentacion(LocalDateTime.now());
        mascotaEntidad.setEstaDormido(true);

        LocalDateTime horaAComparar = LocalDateTime.now().plusSeconds(30);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());
        servicioMascota.actualizarEstadisticas(mascotaDTO, horaAComparar);

        //VERIFICACION
        assertThat(mascotaDTO.getEstaDormido(), equalTo(true));
    }

}





