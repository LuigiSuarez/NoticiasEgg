package com.egg.springeggnoticias.controladores;


import com.egg.springeggnoticias.servicios.NoticiaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControlador {
    @Autowired
    NoticiaServicio noticiaServicio;

    @GetMapping("/dashboard")
    public String panelAdministrativo(){
        return "panel.html";
    }

    @GetMapping("/lista")
    public String listarNoticiasAdmin(ModelMap modelo){

        modelo.addAttribute ("listanoticias", noticiaServicio.listarNoticia ());
        return "inicio.html";
    }

}
