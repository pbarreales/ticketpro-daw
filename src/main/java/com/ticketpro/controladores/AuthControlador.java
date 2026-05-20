package com.ticketpro.controladores;

import com.ticketpro.modelos.Usuario;
import com.ticketpro.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController // 1. Le decimos a Spring que esto es una ventanilla web (API)
public class AuthControlador {

    @Autowired // 2. Spring conecta automáticamente el repositorio aquí (Inyección)
    private UsuarioRepositorio usuarioRepositorio;

    // 3. Cuando alguien envíe datos a la URL "/api/auth/login", se ejecutará este
    // método
    @PostMapping("/api/auth/login")
    public String login(@RequestParam String email, @RequestParam String password) {

        // 4. Buscamos en la base de datos usando el email que nos envían
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(email);

        // 5. PRIMER CONTROL: ¿El email existe en la base de datos?
        if (!usuarioOpt.isPresent()) {
            return "Error: El usuario con ese email no existe.";
        }

        // 6. Si el código sigue aquí, significa que SÍ existe. Sacamos al usuario del
        // envoltorio.
        Usuario usuarioReal = usuarioOpt.get();

        // 7. SEGUNDO CONTROL: ¿La contraseña introducida coincide con la de la base de
        // datos?
        if (usuarioReal.getPassword().equals(password)) {
            return "¡Bienvenido a Ticketpro, " + usuarioReal.getNombre() + "! Login correcto.";
        } else {
            return "Error: La contraseña es incorrecta. Inténtelo de nuevo.";
        }
    }
}