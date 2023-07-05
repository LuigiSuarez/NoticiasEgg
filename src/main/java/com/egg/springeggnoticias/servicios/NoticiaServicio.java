package com.egg.springeggnoticias.servicios;

import com.egg.springeggnoticias.entidades.Imagen;
import com.egg.springeggnoticias.entidades.Noticia;
import com.egg.springeggnoticias.miexception.miException;
import com.egg.springeggnoticias.repositorios.NoticiaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NoticiaServicio {

    @Autowired
    NoticiaRepositorio noticiaRepositorio;

    @Autowired
    ImagenServicio imagenServicio;

    public void crearNoticia(MultipartFile archivo, String titulo, String cuerpo) throws miException {
        validar(0L, titulo, cuerpo);
        Noticia noticia = new Noticia();
        noticia.setTitulo(titulo);
        noticia.setCuerpo(cuerpo);
        Imagen imagen = imagenServicio.guardar(archivo);
        noticia.setImagen(imagen);
        noticiaRepositorio.save(noticia);
    }

    public void modificarNoticia(MultipartFile archivo,Long id, String titulo , String cuerpo) throws miException {
        validar(id,titulo,cuerpo);
        Optional<Noticia> respuesta = noticiaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Noticia noticia = respuesta.get();
            noticia.setTitulo(titulo);
            noticia.setCuerpo(cuerpo);

            String idImagen = null;
            if(noticia.getImagen()!=null){
                idImagen=noticia.getImagen().getId();
                Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
                noticia.setImagen(imagen);
            }else{
                Imagen imagen = imagenServicio.guardar(archivo);
                noticia.setImagen(imagen);
            }
            noticiaRepositorio.save(noticia);
        }
    }



    public Noticia getOne(Long id){
        return noticiaRepositorio.getOne(id);
    }

    public List<Noticia> buscarTitulo(String titulo){
        List<Noticia> aux= noticiaRepositorio.findByTitulo(titulo);
        return aux;
    }

    public List<Noticia> listarNoticia(){
        List<Noticia>listaNoticia = new ArrayList<>();
        listaNoticia = noticiaRepositorio.findAll();
        return listaNoticia;
    }


    private void validar(Long id,String titulo,String cuerpo) throws miException {
        if(id==null){
          throw new miException("el id no puede ser nulo");
        }

        if(titulo==null || titulo.trim().isEmpty()){
          throw new miException("el titulo no pueder ser nulo ni estar vacio");
        }

        if(cuerpo==null || cuerpo.trim().isEmpty()){
            throw new miException("el cuerpo no pueder ser nulo ni estar vacio");
        }
    }


}
