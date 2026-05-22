package com.ticketpro.controladores;

import com.ticketpro.dto.LoginRequest;
import com.ticketpro.dto.RegistroRequest;
import com.ticketpro.servicios.AuthServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthControlador {

    @Autowired
    private AuthServicio authServicio;

    // REGISTRO
    @PostMapping("/api/auth/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest peticion) {
        boolean exito = authServicio.registrarUsuario(peticion);

        if (!exito) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: El email ya está en uso.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("¡Usuario registrado con éxito!");
    }

    // LOGIN
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String resultado = authServicio.autenticarUsuario(loginRequest);

        if (resultado.equals("ERR_NOT_FOUND")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: El usuario con ese email no existe.");
        }

        if (resultado.equals("ERR_BAD_PASSWORD")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: La contraseña es incorrecta. Inténtelo de nuevo.");
        }

        // Si no ha saltado ningún error, el servicio nos devolvió el nombre del usuario
        return ResponseEntity.ok("¡Bienvenido a Ticketpro, " + resultado + "! Login correcto.");
    }
}