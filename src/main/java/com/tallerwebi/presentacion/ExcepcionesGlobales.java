package com.tallerwebi.presentacion;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExcepcionesGlobales {

    //Maneja las excepciones HTTP ERROR 405 Request method not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String manejarMethodNotSupportedExcepcion() {
        return "redirect:/login";
    }

}
