package com.tallerwebi.dominio;

import com.tallerwebi.dominio.mapeado.Coordenada;

public interface ServicioCoordenada {
    Coordenada obtenerCoordenadas(String pais, String provincia);
}
