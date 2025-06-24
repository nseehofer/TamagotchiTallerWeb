package com.tallerwebi.dominio;

import com.tallerwebi.dominio.mapeado.Clima;

public interface ServicioTemperatura {
    Clima getTemperatura(Double latitud, Double longitud);
}
