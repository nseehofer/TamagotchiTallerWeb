package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.ServicioTemperatura;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.EnergiaInsuficiente;
import com.tallerwebi.dominio.excepcion.LimpiezaMaximaException;
import com.tallerwebi.dominio.excepcion.MascotaSatisfecha;
import com.tallerwebi.dominio.mapeado.Clima;
import com.tallerwebi.dominio.excepcion.EnergiaMaxima;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ControladorMascota {

    private ServicioMascota servicioMascota;
    private ModelMap modelo = new ModelMap();
    private ServicioLogin servicioLogin;
    private ServicioTemperatura servicioTemperatura;

    @Autowired
    public ControladorMascota(ServicioMascota servicioMascota, ServicioLogin servicioLogin, ServicioTemperatura servicioTemperatura) {
        this.servicioMascota = servicioMascota;
        this.servicioLogin = servicioLogin;
        this.servicioTemperatura = servicioTemperatura;
    }

    @RequestMapping(path = "/mascota/crearconpost", method = RequestMethod.POST)
    public ModelAndView crearMascota(String nombre,HttpServletRequest request) {
        if(request.getSession().getAttribute("ID") == null){
            return new ModelAndView("redirect:/login");
        }
        String emailDeUsuarioEnSesion = (String) request.getSession().getAttribute("EMAIL");
        Usuario usuarioObtenido = this.servicioLogin.buscarUsuarioPorEmail(emailDeUsuarioEnSesion);
        MascotaDTO mascotaAGuardar = this.servicioMascota.crearMascota(nombre,usuarioObtenido.getId());

        
        Clima climaUrl = this.servicioTemperatura.getTemperatura(request);
      

        if(climaUrl != null) {
            modelo.addAttribute("climaUrl", climaUrl);
            modelo.addAttribute("temperaturaActual",climaUrl.obtenerTemperaturaActual());
            this.servicioMascota.siHaceFrioYLaMascotaEstaDesabrigadaSePuedeEnfermarConMayorProbabilidad(climaUrl, mascotaAGuardar);         
        } else {
            modelo.addAttribute("error", "No se pudo obtener la temperatura. Verifique las coordenadas.");
        }
        //

        //guarda en bd
        MascotaDTO mascotaGuardada = this.servicioMascota.crear(mascotaAGuardar);
        modelo.put("mascota", mascotaGuardada);
        modelo.put("usuario", usuarioObtenido);
        return new ModelAndView("elegirTipoMascota", modelo);
    }

    @RequestMapping(path = "/mascota/jugar", method = RequestMethod.POST)
    public ModelAndView jugar(Long id) {
        MascotaDTO mascota= servicioMascota.traerUnaMascota(id);
        try {
            mascota = servicioMascota.jugar(mascota);
        } catch (EnergiaInsuficiente energiaInsuficiente) {
            modelo.put("error","No podés jugar, te falta energía");
        }

        //actualiza en bd
        //this.servicioMascota.actualizarMascota(mascota);
        modelo.put("mascota", mascota);

        return new ModelAndView("mascota",modelo);
    }

    @RequestMapping(path = "/mascota/traerlistado", method = RequestMethod.GET)
    public ModelAndView mostrarListadoDeMascotas() {
        List<Mascota> mascotas = this.servicioMascota.traerMascotas();
        modelo.put("mascotas",mascotas);
        return new ModelAndView("inicio", modelo);
    }

    @RequestMapping(path = "/mascota/ver", method = RequestMethod.POST)
    public ModelAndView verMascota(Long id, HttpServletRequest request) {
        MascotaDTO mascota = servicioMascota.traerUnaMascota(id);
        String emailDeUsuarioEnSesion = (String) request.getSession().getAttribute("EMAIL");
        Usuario usuarioObtenido = this.servicioLogin.buscarUsuarioPorEmail(emailDeUsuarioEnSesion);
        Clima climaUrl = this.servicioTemperatura.getTemperatura(request);


        if(climaUrl != null) {
            modelo.addAttribute("climaUrl", climaUrl);
            modelo.addAttribute("temperaturaActual", climaUrl.obtenerTemperaturaActual());
            
            this.servicioMascota.siHaceFrioYLaMascotaEstaDesabrigadaSePuedeEnfermarConMayorProbabilidad(climaUrl, mascota);           
        } else {
            modelo.addAttribute("error", "No se pudo obtener la temperatura. Verifique las coordenadas.");
        }
        modelo.put("mascota", mascota);
        modelo.put("usuario",usuarioObtenido);
        return new ModelAndView("mascota", modelo);
    }

    @RequestMapping(path = "/mascota/limpiar", method = RequestMethod.POST)
    public ModelAndView limpiarMascota(Long id) {
        MascotaDTO mascota = servicioMascota.traerUnaMascota(id);

        try {
            servicioMascota.limpiarMascota(mascota);
        } catch (LimpiezaMaximaException limpiezaMaxima) {
            modelo.put("error", "La higiene se encuentra al maximo");
        }

        modelo.put("mascota", mascota);

        return new ModelAndView("mascota", modelo);
    }

    @RequestMapping(path = "/mascota/alimentar", method = RequestMethod.POST)
    public ModelAndView alimentar(Long id) {
       MascotaDTO mascota= servicioMascota.traerUnaMascota(id);

        try {
            mascota = servicioMascota.alimentar(mascota);
        } catch (MascotaSatisfecha mascotaSatisfecha) {
            modelo.put("error","Tu mascota está satisfecha");
        }

        modelo.put("ultimaAlimentacion", mascota.getUltimaAlimentacion());
        modelo.put("mascota", mascota);

        return new ModelAndView("mascota",modelo);
    }

    @RequestMapping(path = "/mascota/dormir", method = RequestMethod.POST)
    public ModelAndView dormir(Long id) {
        MascotaDTO mascota= servicioMascota.traerUnaMascota(id);
        try {
            mascota = servicioMascota.dormir(mascota);
        } catch (EnergiaMaxima energiaMaxima) {
            modelo.put("error","No se puede dormir porque no tiene sueño");
        }

        //actualiza en bd
        //this.servicioMascota.actualizarMascota(mascota);
        modelo.put("mascota", mascota);

        return new ModelAndView("mascota",modelo);
    }

    @RequestMapping(path = "/mascota/cementerio", method = RequestMethod.GET)
    public ModelAndView verMascotaMuerta(Long id) {
        MascotaDTO mascota = servicioMascota.traerUnaMascota(id);
        if (mascota == null || mascota.getEstaVivo()) {

            return new ModelAndView("redirect:/home");
        }
        modelo.put("mascota", mascota);
        return new ModelAndView("cementerio", modelo);
    }

    @RequestMapping(path = "/mascota/asignar-tipo", method = RequestMethod.POST)
    public ModelAndView asignarTipoDeMascota(Long id, String tipo) {
        MascotaDTO mascota = servicioMascota.traerUnaMascota(id);
        mascota.setTipo(tipo);
        servicioMascota.actualizarMascota(mascota);

        modelo.put("mascota", mascota);
        return new ModelAndView("mascota", modelo);
    }




}
