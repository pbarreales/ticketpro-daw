package com.ticketpro.controladores;

import com.ticketpro.dto.LoginRequest;
import com.ticketpro.dto.RegistroRequest;
import com.ticketpro.modelos.Usuario;
import com.ticketpro.servicios.AuthServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthControlador {

    @Autowired
    private AuthServicio authServicio;

    @PostMapping("/api/auth/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest peticion) {
        boolean exito = authServicio.registrarUsuario(peticion);

        if (!exito) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: El email ya está en uso.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("¡Usuario registrado con éxito!");
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = authServicio.autenticarUsuario(loginRequest);

            // =========================================================
            // 🚀 ZONA DE ÉXITO: Inyectamos el ID que le faltaba al JS
            // =========================================================
            java.util.Map<String, String> respuestaJson = new java.util.HashMap<>();

            respuestaJson.put("usuarioId", String.valueOf(usuario.getId())); // 🌟 LA LLAVE MAESTRA DE 64 BITS
            respuestaJson.put("nombre", usuario.getNombre());
            respuestaJson.put("rol", usuario.getRol());

            return ResponseEntity.ok(respuestaJson);

        } catch (RuntimeException e) {
            String mensajeError = e.getMessage();

            if ("ERR_NOT_FOUND".equals(mensajeError)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Error: El usuario con ese email no existe.");
            }

            if ("ERR_BAD_PASSWORD".equals(mensajeError)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Error: La contraseña es incorrecta. Inténtelo de nuevo.");
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }
}