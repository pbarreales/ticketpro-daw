package com.ticketpro.controladores;

import com.ticketpro.modelos.Usuario;
import com.ticketpro.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // 🌟 ENDPOINT 1: Listar todos los usuarios para el panel de administración
    // URL: http://localhost:8080/api/usuarios
    @GetMapping("/api/usuarios")
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    // 🌟 ENDPOINT 2: Cambiar el rol de un usuario en seco en la Base de Datos
    // URL: http://localhost:8080/api/usuarios/4/rol?nuevoRol=INFORMATICO
    @PutMapping("/api/usuarios/{id}/rol")
    public Usuario cambiarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        // Buscamos al usuario por su ID primario
        Usuario usuario = usuarioRepositorio.findById(id).orElseThrow();

        // Modificamos el rol en caliente
        usuario.setRol(nuevoRol);

        // Guardamos la mutación en MySQL
        return usuarioRepositorio.save(usuario);
    }
}