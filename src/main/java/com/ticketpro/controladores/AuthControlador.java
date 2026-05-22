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

        // 1. Creamos el mapa que acertaste antes (Importa java.util.HashMap y
        // java.util.Map arriba)
        java.util.Map<String, String> respuestaJson = new java.util.HashMap<>();

        // 2. Metemos el nombre (que es la variable 'resultado')
        respuestaJson.put("nombre", resultado);

        // 3. TRUCO TEMPORAL: Le colamos el rol ADMIN a piñón para probar el dashboard
        // camaleónico
        respuestaJson.put("rol", "ADMIN");

        // 4. Devolvemos el mapa. Spring Boot lo transformará en el JSON que espera
        // JavaScript
        return ResponseEntity.ok(respuestaJson);
    }

}