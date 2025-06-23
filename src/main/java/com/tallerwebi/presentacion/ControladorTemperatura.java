package com.tallerwebi.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tallerwebi.dominio.ServicioTemperatura;
@Controller
public class ControladorTemperatura {

    private final ServicioTemperatura servicioTemperatura;
    
    @Autowired
    public ControladorTemperatura(ServicioTemperatura servicioTemperatura) {
        this.servicioTemperatura = servicioTemperatura;
    }

    @RequestMapping("/mascota")
    public String vistaMascota() {
        return "mascota";
    }

    @RequestMapping(path = "/obtener-temperatura", method = RequestMethod.POST)
    public String mostrarTemperatura(@RequestParam("latitud") Double latitud, @RequestParam("longitud") Double longitud,
            Model model) {
        String temperaturaUrl = this.servicioTemperatura.getTemperatura(latitud, longitud);
        model.addAttribute("temperaturaUrl", temperaturaUrl);
        return "mascota";
    }
}
