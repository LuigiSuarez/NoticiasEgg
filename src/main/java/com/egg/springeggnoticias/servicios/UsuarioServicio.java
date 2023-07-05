package com.egg.springeggnoticias.servicios;

import com.egg.springeggnoticias.entidades.Imagen;
import com.egg.springeggnoticias.entidades.Usuario;
import com.egg.springeggnoticias.enumeraciones.Rol;
import com.egg.springeggnoticias.miexception.miException;
import com.egg.springeggnoticias.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;


    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String email, String password, String password2) throws miException {

        validar(nombre, email, password, password2);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);

        Imagen imagen = imagenServicio.guardar(archivo);

        usuario.setImagen(imagen);

        usuarioRepositorio.save(usuario);
    }


    public void actualizar(MultipartFile archivo,String idUsuario, String nombre, String email, String password,
                           String password2) throws miException{
        validarModificar(nombre, email, password, password2);
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));
            usuario.setRol(Rol.USER);
            String idImagen = null;
            if(usuario.getImagen()!=null){
              idImagen=usuario.getImagen().getId();
              Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
              usuario.setImagen(imagen);

            }else{
                Imagen imagen = imagenServicio.guardar(archivo);
                usuario.setImagen(imagen);
            }
            usuarioRepositorio.save(usuario);
        }
    }


    public Usuario buscarUsuario(String id){
        Usuario usuario = new Usuario();
            if (id!=null) {
                usuario = usuarioRepositorio.getOne(id);
            }
            return usuario;
        }


    public void validar(String nombre, String email, String password, String password2) throws miException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email.trim());
        if (usuario != null) {
            throw new miException("el email ya esta registrado");
        }

        if (nombre.isEmpty() || nombre == null) {
            throw new miException("el nombre de usuario no puede ser nullo ni estar vacio");
        }
        if (email.isEmpty() || email == null) {
            throw new miException("el email de usuario no puede ser nullo ni estar vacio");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new miException("el pasword de usuario no puede ser nullo ni estar vacio");
        }
        if (!password.equals(password2)) {
            throw new miException("las contrasenas deben iguales");
        }
    }

    public void validarModificar(String nombre, String email, String password, String password2) throws miException {


        if (nombre.isEmpty() || nombre == null) {
            throw new miException("el nombre de usuario no puede ser nullo ni estar vacio");
        }
        if (email.isEmpty() || email == null) {
            throw new miException("el email de usuario no puede ser nullo ni estar vacio");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new miException("el pasword de usuario no puede ser nullo ni estar vacio");
        }
        if (!password.equals(password2)) {
            throw new miException("las contrasenas deben iguales");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority       p        = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);



            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }
    }



}
