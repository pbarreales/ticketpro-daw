package com.ticketpro.servicios;

import com.ticketpro.dto.LoginRequest;
import com.ticketpro.dto.RegistroRequest;
import com.ticketpro.modelos.Usuario;
import com.ticketpro.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public boolean registrarUsuario(RegistroRequest peticion) {
        // Comprobamos si el email ya existe en la BD
        Optional<Usuario> usuarioExistente = usuarioRepositorio.findByEmail(peticion.getEmail());

        if (usuarioExistente.isPresent()) {
            return false; // Error: email en uso, el registro falla
        }

        // Si está libre, creamos el modelo definitivo para MySQL
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(peticion.getNombre());
        nuevoUsuario.setEmail(peticion.getEmail());
        nuevoUsuario.setPassword(peticion.getPassword());
        nuevoUsuario.setRol("Usuario"); // Fijado automáticamente a piñón por el servidor

        usuarioRepositorio.save(nuevoUsuario);
        return true; // Registro completado con éxito
    }

    // 🌟 MODIFICADO: Ahora devuelve un objeto Usuario completo
    public Usuario autenticarUsuario(LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(loginRequest.getEmail());

        // Control tradicional: si NO está presente, lanzamos la excepción
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("ERR_NOT_FOUND");
        }

        // Si el código sigue vivo, extraemos el usuario de la caja fuerte con .get()
        Usuario usuarioReal = usuarioOpt.get();

        // Verificamos la contraseña usando tus variables reales
        if (usuarioReal.getPassword().equals(loginRequest.getPassword())) {
            return usuarioReal; // 🏆 ÉXITO: Devolvemos el usuario completo con su ROL de la BD
        } else {
            throw new RuntimeException("ERR_BAD_PASSWORD"); // Contraseña mal introducida
        }
    }
}