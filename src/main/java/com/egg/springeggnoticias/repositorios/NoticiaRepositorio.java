package com.egg.springeggnoticias.repositorios;

import com.egg.springeggnoticias.entidades.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepositorio extends JpaRepository<Noticia,Long> {

    @Query("select n from Noticia n where lower(n.titulo) like %:titulo%")
    public List<Noticia> findByTitulo(@Param("titulo") String titulo);

}
