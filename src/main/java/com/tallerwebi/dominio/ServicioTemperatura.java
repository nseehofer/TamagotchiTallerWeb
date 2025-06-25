package com.tallerwebi.dominio;

import javax.servlet.http.HttpServletRequest;

import com.tallerwebi.dominio.mapeado.Clima;

public interface ServicioTemperatura {
    Clima getTemperatura(HttpServletRequest sessionUsuario);
}
