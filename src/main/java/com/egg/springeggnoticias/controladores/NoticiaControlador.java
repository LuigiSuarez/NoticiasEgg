package com.egg.springeggnoticias.controladores;

import com.egg.springeggnoticias.entidades.Noticia;
import com.egg.springeggnoticias.entidades.Usuario;
import com.egg.springeggnoticias.miexception.miException;
import com.egg.springeggnoticias.servicios.NoticiaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/noticia")
public class NoticiaControlador {

    @Autowired
    NoticiaServicio noticiaServicio;

//    metodos para crear noticias

    @GetMapping("/crear")
    public String crearNoticia() {
        return "noticia_form.html";
    }

    @PostMapping("/enviar")
    public String enviarNoticia(MultipartFile archivo, @RequestParam(required = false) String titulo_noticia,
                                @RequestParam(required =
            false) String cuerpo_noticia, ModelMap modelo) {
        try {
            noticiaServicio.crearNoticia (archivo,titulo_noticia, cuerpo_noticia);
            modelo.put ("exito", "se guardo la noticia con exito");
        } catch (miException e) {
            modelo.put ("error", e.getMessage ());
            return "noticia_form.html";
        }
        return "noticia_form.html";

    }

//    metodos para listar noticias noticias vista--> listar modificar

    @GetMapping("listar")
    public String listarNoticia(ModelMap modelo, @ModelAttribute("exito") String some) {
        modelo.addAttribute ("exito", some);
        modelo.addAttribute ("lista_noticias", noticiaServicio.listarNoticia ());
        return "noticia_listar_modificar.html";
    }

// metodo para poder modificar y recibir el id por path url

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {
        modelo.addAttribute ("noticia_modificar", noticiaServicio.getOne (id));
        return "noticia_modificar.html";
    }

    //    metodo para enviar la noticia modificada
    //para usar redirect agregar un parametro de la clase RedirectAttributes
    // usar  redirAttr.addFlashAttribute y llenar los valores con la variable y su mensaje
    // por ultimo  @ModelAttribute("exito") String some usar esto en el lugar donde redireciona
    // usar un modelo para cargar el valor asi  modelo.addAttribute("error", some);
    @PostMapping("/modificar/{id}")
    public String modificar(MultipartFile archivo,@PathVariable Long id, String titulo, String cuerpo, ModelMap modelo,
                             RedirectAttributes redirAttr, @ModelAttribute("error") String some){

        try {
            noticiaServicio.modificarNoticia (archivo,id, titulo, cuerpo);
            redirAttr.addFlashAttribute ("exito", "se modifico la noticia con exito");
            return "redirect:../listar";

        } catch (miException e) {
            redirAttr.addFlashAttribute ("error", e.getMessage ());
            modelo.addAttribute ("error", some);
            return "redirect:../modificar/{id}";
        }
    }

    //focus de noticia del index
    @GetMapping("/focus/{id}")
    public String focus(@PathVariable Long id, ModelMap modelo) {
        modelo.put ("noticia_focus", noticiaServicio.getOne (id));
        return "noticia_focus.html";

    }

    //metodos para busqueda

    @GetMapping("/busqueda")
    public String busquedaVista(Model model) {
        model.addAttribute ("noticia", new Noticia ());
        return "noticia_busqueda";
    }

    @GetMapping("/resultado")
    public String buscarTitulo(@RequestParam String titulo, Model model, @ModelAttribute("noticia") Noticia noticia) {
        model.addAttribute ("noticiasPorTitulo", noticiaServicio.buscarTitulo (titulo));
        System.out.println (noticiaServicio.buscarTitulo ("dos")
                                           .toString ());
        return "noticia_busqueda";
    }




}
