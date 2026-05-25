package com.ticketpro.controladores;

import com.ticketpro.dto.LoginRequest;
import com.ticketpro.dto.RegistroRequest;
import com.ticketpro.modelos.Usuario; // 🌟 IMPORTANTE: Añadido para poder usar la entidad
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

    // LOGIN (Saneado y adaptado a Excepciones)
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Intentamos autenticar. Si algo falla, el servicio lanzará un "puñetazo" al
            // catch de abajo
            Usuario usuario = authServicio.autenticarUsuario(loginRequest);

            // =========================================================
            // 🚀 ZONA DE ÉXITO: El usuario es real y el login es correcto
            // =========================================================
            java.util.Map<String, String> respuestaJson = new java.util.HashMap<>();

            // Rellenamos el mapa con los datos reales extraídos de la base de datos
            respuestaJson.put("nombre", usuario.getNombre());
            respuestaJson.put("rol", usuario.getRol()); // 🌟 ¡ADIÓS CONTRABANDO! Rol real de MySQL

            return ResponseEntity.ok(respuestaJson);

        } catch (RuntimeException e) {
            // Capturamos el error y miramos el texto del mensaje para responder al Front
            String mensajeError = e.getMessage();

            if ("ERR_NOT_FOUND".equals(mensajeError)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Error: El usuario con ese email no existe.");
            }

            if ("ERR_BAD_PASSWORD".equals(mensajeError)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Error: La contraseña es incorrecta. Inténtelo de nuevo.");
            }

            // Por si ocurre cualquier otro desastre informático
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }
}