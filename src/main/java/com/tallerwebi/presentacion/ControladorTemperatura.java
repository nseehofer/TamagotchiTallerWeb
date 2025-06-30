package com.tallerwebi.presentacion;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tallerwebi.dominio.ServicioCoordenada;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.ServicioTemperatura;
import com.tallerwebi.dominio.mapeado.Clima;
@Controller
public class ControladorTemperatura {

    private ServicioTemperatura servicioTemperatura;
    private ServicioMascota servicioMascota;
    private ServicioCoordenada servicioCoordenada;
    
    @Autowired
    public ControladorTemperatura(ServicioTemperatura servicioTemperatura, ServicioMascota servicioMascota, ServicioCoordenada servicioCoordenada) {
        this.servicioTemperatura = servicioTemperatura;
        this.servicioMascota = servicioMascota;
        this.servicioCoordenada = servicioCoordenada;
    }

    @RequestMapping("/mascota")
    public String vistaMascota() {
        return "mascota";
    }

    @RequestMapping(path = "/mascota", method = RequestMethod.GET)
    public String mostrarTemperatura(HttpServletRequest sessionUsuario,
            Model model) {
        Clima climaUrl = this.servicioTemperatura.getTemperatura(sessionUsuario);
        // HARDCODEO EL ID PARA TEST
        // DEBO GUARDAR EL ID DE MASCOTA EN SESSION Y PASAR EL HTTP POR PARAMETRO 
        MascotaDTO mascota= this.servicioMascota.traerUnaMascota(1L);
        
        if(climaUrl != null) {
            model.addAttribute("climaUrl", climaUrl);
            model.addAttribute("temperaturaActual", climaUrl.obtenerTemperaturaActual());
            model.addAttribute("mascota", mascota);
           
        } else {
            model.addAttribute("error", "No se pudo obtener la temperatura. Verifique las coordenadas.");
        }

        return "mascota";
    }
}
