package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import com.tallerwebi.dominio.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ControladorHome {

    private ServicioMascota servicioMascota;
    private ModelMap modelo = new ModelMap();
    private ServicioLogin servicioLogin;

    @Autowired
    public ControladorHome(ServicioMascota servicioMascota, ServicioLogin servicioLogin) {
        this.servicioMascota = servicioMascota;
        this.servicioLogin = servicioLogin;
    }


    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView irAHome(HttpServletRequest request) {
        if(request.getSession().getAttribute("ID") == null){
            return new ModelAndView("redirect:/login");
        }
        Long idUsuario = (Long) request.getSession().getAttribute("ID");
        List<Mascota> mascotas = this.servicioMascota.traerMascotasDeUnUsuario(idUsuario);
        String emailDeUsuarioEnSesion = (String) request.getSession().getAttribute("EMAIL");
        Usuario usuarioObtenido = this.servicioLogin.buscarUsuarioPorEmail(emailDeUsuarioEnSesion);
        modelo.put("mascotas",mascotas);
        modelo.put("idUsuario",idUsuario);
        modelo.put("nombreUsuario",request.getSession().getAttribute("NOMBRE"));
        modelo.put("usuario", usuarioObtenido);
        return new ModelAndView("home",modelo);
    }
}
