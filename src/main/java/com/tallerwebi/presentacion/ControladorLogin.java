package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorLogin {

    private ServicioLogin servicioLogin;

    @Autowired
    public ControladorLogin(ServicioLogin servicioLogin) {
        this.servicioLogin = servicioLogin;
    }

    @RequestMapping("/login")
    public ModelAndView irALogin(HttpServletRequest request) {

        if (request.getSession().getAttribute("ID") != null) {
            return new ModelAndView("redirect:/home");
        }
        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLogin());
        return new ModelAndView("login", modelo);
    }

    @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
    public ModelAndView validarLogin(@ModelAttribute("datosLogin") DatosLogin datosLogin, HttpServletRequest request) {
        ModelMap model = new ModelMap();

        Usuario usuarioBuscado = servicioLogin.consultarUsuario(datosLogin.getEmail(), datosLogin.getPassword());
        if (usuarioBuscado != null) {
            // request.getSession().setAttribute("ROL", usuarioBuscado.getRol());

            request.getSession().setAttribute("NOMBRE", usuarioBuscado.getNombre());
            request.getSession().setAttribute("ID", usuarioBuscado.getId());
            // AGREGO EMAIL A LA SESION PARA DESPUES OBTENER EL USUARIO CON EL REPOSITORIO
            request.getSession().setAttribute("EMAIL", usuarioBuscado.getEmail());

            return new ModelAndView("redirect:/home");
        } else {
            model.put("error", "Usuario o clave incorrecta");
        }
        return new ModelAndView("login", model);
    }

    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Usuario usuario,HttpServletRequest request) {
        if (request.getSession().getAttribute("ID") != null) {
            return new ModelAndView("redirect:/home");
        }
        ModelMap model = new ModelMap();
        try {
            // Por defecto el rol es user
            usuario.setRol("USER");
            usuario.setMonedas(100.00);
            servicioLogin.registrar(usuario);
        } catch (UsuarioExistente e) {
            model.put("error", "El usuario ya existe");
            return new ModelAndView("nuevo-usuario", model);
        } catch (Exception e) {
            model.put("error", "Error al registrar el nuevo usuario");
            return new ModelAndView("nuevo-usuario", model);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario(HttpServletRequest request) {
        if (request.getSession().getAttribute("ID") != null) {
            return new ModelAndView("redirect:/home");
        }
        ModelMap model = new ModelMap();
        model.put("usuario", new Usuario());
        return new ModelAndView("nuevo-usuario", model);
    }

    @RequestMapping(path = "/cerrar-sesion", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView inicio(HttpServletRequest request) {
        if (request.getSession().getAttribute("ID") != null) {
            return new ModelAndView("redirect:/home");
        }

        return new ModelAndView("presentacion");
    }

}
