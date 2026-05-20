package com.ticketpro.controladores;

import com.ticketpro.modelos.Usuario;
import com.ticketpro.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
public class AuthControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Clase auxiliar interna (DTO) para empaquetar los datos que vienen ocultos en
    // el Body
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters y Setters necesarios para que Spring Boot mapee el JSON
        // automáticamente
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        // 4. Buscamos en la base de datos usando el email que viene dentro del objeto
        // seguro
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(loginRequest.getEmail());

        // 5. PRIMER CONTROL: Si no existe, devolvemos un estado 404 (Not Found) o 401
        // (Unauthorized)
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: El usuario con ese email no existe.");
        }

        Usuario usuarioReal = usuarioOpt.get();

        // 7. SEGUNDO CONTROL: ¿La contraseña coincide?
        if (usuarioReal.getPassword().equals(loginRequest.getPassword())) {
            // Si es correcto, devolvemos un glorioso 200 OK con el mensaje de bienvenida
            return ResponseEntity.ok("¡Bienvenido a Ticketpro, " + usuarioReal.getNombre() + "! Login correcto.");
        } else {
            // Si la contraseña falla, devolvemos un estado 401 (No autorizado) en vez de un
            // 200 camuflado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: La contraseña es incorrecta. Inténtelo de nuevo.");
        }
    }
}