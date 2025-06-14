package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.Mascota;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioMascota {
    Long crear(Mascota obtenerEntidad);
    Mascota obtenerPor(Long id);
    List<Mascota> obtenerListaDeMascotas();
    void actualizar(Mascota mascota);

}
