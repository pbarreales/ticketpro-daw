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

    // 🔑 LA HERRAMIENTA GLOBAL DE ENCRIPTACIÓN
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 🔐 REGISTRO DE USUARIOS (Con blindaje de Email y Hash de Contraseña)
     */
    public boolean registrarUsuario(RegistroRequest peticion) {

        // 🛡️ 1. FILTRO DE CORREOS FALSOS (Fail-Fast Regex): Bloquea cosas como
        // usuario@gmail
        if (peticion.getEmail() == null
                || !peticion.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return false; // Abortamos la operación de inmediato si el correo es inválido
        }

        // 2. Comprobamos si el email ya existe en la BD
        Optional<Usuario> usuarioExistente = usuarioRepositorio.findByEmail(peticion.getEmail());

        if (usuarioExistente.isPresent()) {
            return false; // Error: email en uso, el registro falla
        }

        // 3. Si todo está correcto, mapeamos los datos al modelo de MySQL
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(peticion.getNombre());
        nuevoUsuario.setEmail(peticion.getEmail());

        // 🔥 TRITURADORA BCRYPT: Encriptamos la contraseña antes del save
        String contraseñaEncriptada = encoder.encode(peticion.getPassword());
        nuevoUsuario.setPassword(contraseñaEncriptada);

        nuevoUsuario.setRol("USUARIO"); // Fijado automáticamente a piñón por el servidor

        usuarioRepositorio.save(nuevoUsuario);
        return true; // Registro completado con éxito
    }

    /**
     * 🔓 LOGIN DE USUARIOS (Verificación criptográfica de credenciales)
     */
    public Usuario autenticarUsuario(LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(loginRequest.getEmail());

        // Control tradicional: si NO está presente, lanzamos la excepción
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("ERR_NOT_FOUND");
        }

        // Si el código sigue vivo, extraemos el usuario de la caja fuerte con .get()
        Usuario usuarioReal = usuarioOpt.get();

        // 🔥 COMPARACIÓN CRIPTOGRÁFICA: Comparamos texto plano con el hash de MySQL
        if (encoder.matches(loginRequest.getPassword(), usuarioReal.getPassword())) {
            return usuarioReal; // 🏆 ÉXITO: Devolvemos el usuario completo con su ROL de la BD
        } else {
            throw new RuntimeException("ERR_BAD_PASSWORD"); // Contraseña mal introducida
        }
    }
}