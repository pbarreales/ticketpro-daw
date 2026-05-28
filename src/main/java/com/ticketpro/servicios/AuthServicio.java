package com.ticketpro.servicios;

import com.ticketpro.dto.LoginRequest;
import com.ticketpro.dto.RegistroRequest;
import com.ticketpro.modelos.Usuario;
import com.ticketpro.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Instancia compartida
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Devuelve true si el registro fue exitoso, false si el email es invalido o ya
    // existe
    public boolean registrarUsuario(RegistroRequest peticion) {

        // Validación mínima de formato de email antes de consultar la BD
        if (peticion.getEmail() == null
                || !peticion.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return false;
        }

        Optional<Usuario> usuarioExistente = usuarioRepositorio.findByEmail(peticion.getEmail());
        if (usuarioExistente.isPresent()) {
            return false;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(peticion.getNombre());
        nuevoUsuario.setEmail(peticion.getEmail());
        nuevoUsuario.setPassword(encoder.encode(peticion.getPassword()));
        nuevoUsuario.setRol("USUARIO"); // El rol lo asigna el admin después desde el panel
        usuarioRepositorio.save(nuevoUsuario);
        return true;
    }

    // Devuelve el Usuario completo (con su rol) si las credenciales son correctas.
    // Lanza RuntimeException con códigos de error que AuthControlador traduce a
    // HTTP.
    public Usuario autenticarUsuario(LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(loginRequest.getEmail());

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("ERR_NOT_FOUND");
        }

        Usuario usuarioReal = usuarioOpt.get();

        // encoder.matches compara el texto plano con el hash guardado en la BD
        if (encoder.matches(loginRequest.getPassword(), usuarioReal.getPassword())) {
            return usuarioReal;
        } else {
            throw new RuntimeException("ERR_BAD_PASSWORD");
        }
    }
}