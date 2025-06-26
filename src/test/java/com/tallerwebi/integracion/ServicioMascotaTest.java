package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.ServicioMascotaImp;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.presentacion.MascotaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

public class ServicioMascotaTest {

    private RepositorioMascota repositorioMascota;
    private ServicioMascota servicioMascota;

    @BeforeEach
    public void inicializar() {
        this.repositorioMascota = mock(RepositorioMascota.class);
        this.servicioMascota = new ServicioMascotaImp(this.repositorioMascota);
    }

    @Test
    public void cuandoLimpioLaMascotaSuHigieneSeAumentaAlMaximo() throws LimpiezaMaximaException {
        //Preparación
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(20.0);

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
    public void queLasEstadisticasBajenProgresivamenteConElTiempo()  {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);

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
    public void queAlPasarUnTiempoExcesivamenteLargoLasEstadisticasNoDisminuyenMenosQueCero() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setHambre(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);

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

    }

    @Test
    public void queAlJugarDisminuye25PuntosDeHigiene() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.jugar(mascotaDTO);

        //VERIFICACION
        assertThat(mascotaDTO.getHigiene(), equalTo(75.0));
    }

    @Test
    public void cuandoLaMascotaSeDuermeYTieneEnergiaBajaSeLeSuma25() throws EnergiaMaxima{
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(60.0);
        mascotaEntidad.setFelicidad(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());
        servicioMascota.dormir(mascotaDTO);

        //VERIFICACION
        assertThat(mascotaDTO.getEnergia(), equalTo(85.0));
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
    public void cuandoDuermeYTiene75DeEnergiaSubeSoloHasta100() throws EnergiaMaxima {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(75.0);
        mascotaEntidad.setFelicidad(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.dormir(mascotaDTO);

        //VERIFICACION
        assertThat(mascotaDTO.getEnergia(), equalTo(100.0));
    }

    @Test
    public void queAlDormirDisminuye25PuntosDeFelicidad() throws EnergiaMaxima {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setEnergia(60.0);
        mascotaEntidad.setFelicidad(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        //ACCION
        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.dormir(mascotaDTO);

        //VERIFICACION
        assertThat(mascotaDTO.getFelicidad(), equalTo(75.0));
    }


    @Test
    public void queAlJugarDisminuye25PuntosDeEnergia() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);
        mascotaEntidad.setEnergia(100.0);
        mascotaEntidad.setFelicidad(100.0);

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

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.chequearSalud(mascotaDTO);

        assertThat(mascotaDTO.getEstaEnfermo(), equalTo(true));

    }


    /*

    Pendiente para probar con Mockito Spy

    @Test
    public void queLaMascotaSePuedaEnfermarConEstadisticasMedias () {

        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(70.0);
        mascotaEntidad.setEnergia(70.0);
        mascotaEntidad.setFelicidad(70.0);
        mascotaEntidad.setHambre(70.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        servicioMascota.chequearSalud(mascotaDTO);

        assertThat(mascotaDTO.getEstaEnfermo(), equalTo(true));

    }
    */
}
