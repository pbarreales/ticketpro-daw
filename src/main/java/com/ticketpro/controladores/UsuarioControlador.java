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

    // GET /api/usuarios — usado por el panel de administración para listar todo el personal
    @GetMapping("/api/usuarios")
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    // PUT /api/usuarios/{id}/rol?nuevoRol=INFORMATICO
    // Roles posibles: USUARIO, INFORMATICO, ADMIN
    @PutMapping("/api/usuarios/{id}/rol")
    public Usuario cambiarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        Usuario usuario = usuarioRepositorio.findById(id).orElseThrow();
        usuario.setRol(nuevoRol);
        return usuarioRepositorio.save(usuario);
    }
}