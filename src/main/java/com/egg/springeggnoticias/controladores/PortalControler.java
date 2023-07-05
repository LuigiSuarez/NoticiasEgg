package com.egg.springeggnoticias.controladores;

import com.egg.springeggnoticias.entidades.Usuario;
import com.egg.springeggnoticias.miexception.miException;
import com.egg.springeggnoticias.servicios.NoticiaServicio;
import com.egg.springeggnoticias.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/")
public class PortalControler {

    @Autowired
    NoticiaServicio noticiaServicio;
    @Autowired
    UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String index() {

        return "index.html";
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "usuario_registrar.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam String email, @RequestParam String password,
                           @RequestParam String password2, ModelMap modelo, MultipartFile archivo) {
        try {
            usuarioServicio.registrar (archivo,nombre, email, password, password2);
            modelo.put ("exito", "usuario registrado correctamente");
            return "usuario_registrar.html";
        } catch (miException e) {
            modelo.put ("error", e.getMessage());
            return "usuario_registrar.html";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam (required = false) String error, ModelMap modelo) {
        if (error!=null) {
            modelo.put("error", "Usuario o Contrasena invalidos");
        }
        return "usuario_login.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap modelo){
        modelo.addAttribute ("listanoticias", noticiaServicio.listarNoticia ());
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
//        logueado.getRol().toString().equals("ADMIN"))
        if (logueado != null && logueado.getRol() != null && logueado.getRol().toString().equals("ADMIN")){
            return "redirect:/admin/dashboard";
        }
        return "inicio.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/perfil")
    public String perfil(ModelMap modelo,HttpSession session,@ModelAttribute ("exito") String mensaje){
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.put("usuario", usuarioServicio.buscarUsuario(usuario.getId()));
        modelo.put("exito", mensaje);
        return "perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/perfil/modificar")
    public String perfilModificar( ModelMap modelo, HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.put("usuario", usuarioServicio.buscarUsuario(usuario.getId()));
        return "perfil_modificar.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/perfil/modificar/{id}")
    public String perfil(@PathVariable String id, String nombre, String email, String password,
                         String password2, MultipartFile archivo, RedirectAttributes redirAttr){
        try {
            usuarioServicio.actualizar(archivo, id, nombre,email,password,password2);
            redirAttr.addFlashAttribute("exito","se modifico el perfil con exito");
            return  "redirect:/perfil";
        } catch (miException e) {
            redirAttr.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil/modificar";

        }

    }

//    falta el post

}
