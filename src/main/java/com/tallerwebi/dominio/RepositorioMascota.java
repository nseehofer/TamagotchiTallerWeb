package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.Mascota;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioMascota {
    Long crear(Mascota obtenerEntidad);
    Mascota obtenerPor(Long id);
    List<Mascota> obtenerListaDeMascotas();
    List<Mascota> obtenerListaDeMascotasDeUnUsuario(Long idUsuario);
    void actualizar(Mascota mascota);


    Double traerMonedasPorIDMascota(Long id);

    void actualizarMonedas(Double monedas, Long id);
}
