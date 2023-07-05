package com.egg.springeggnoticias.repositorios;

import com.egg.springeggnoticias.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario,String> {

    @Query("select u from Usuario u where u.email=:email")
    public Usuario buscarPorEmail(@Param("email") String email);





}
