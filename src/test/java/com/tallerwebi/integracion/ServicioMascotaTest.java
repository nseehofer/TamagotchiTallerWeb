package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.dominio.implementacion.ServicioMascotaImp;
import com.tallerwebi.presentacion.MascotaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void queLaHigieneBajeProgresivamenteConElTiempo() throws LimpiezaMaximaException {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        //ACCION
        LocalDateTime ultimaHoraHigiene = LocalDateTime.now().minusHours(3);
        LocalDateTime horaActualDeHigiene = LocalDateTime.now();

        mascotaDTO.setUltimaHigiene(ultimaHoraHigiene);

        servicioMascota.actualizarHigiene(mascotaDTO, horaActualDeHigiene);

        //VERIFICACION
        assertThat(mascotaDTO.getHigiene() < 100.0, equalTo(true));

    }

    @Test
    public void queAlUnTiempoExcesivamenteLargoLaHigieneNoDisminuyeMenosQueCero() {
        //PREPARACION
        Mascota mascotaEntidad = new Mascota();
        mascotaEntidad.setId(1L);
        mascotaEntidad.setHigiene(100.0);

        when(repositorioMascota.obtenerPor(mascotaEntidad.getId())).thenReturn(mascotaEntidad);

        MascotaDTO mascotaDTO = servicioMascota.traerUnaMascota(mascotaEntidad.getId());

        //ACCION
        LocalDateTime ultimaHoraHigiene = LocalDateTime.now().minusDays(10);
        LocalDateTime horaActualDeHigiene = LocalDateTime.now();

        mascotaDTO.setUltimaHigiene(ultimaHoraHigiene);

        servicioMascota.actualizarHigiene(mascotaDTO, horaActualDeHigiene);

        //VERIFICACION
        assertThat(mascotaDTO.getHigiene() == 0.0, equalTo(true));

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
}
