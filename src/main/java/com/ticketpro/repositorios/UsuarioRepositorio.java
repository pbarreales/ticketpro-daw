package com.ticketpro.repositorios;

import com.ticketpro.modelos.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // Punto de entrada del login: busca por email para luego validar la contraseña con BCrypt
    Optional<Usuario> findByEmail(String email);

    // Para cargar desplegables de asignación filtrados por rol (ej: INFORMATICO)
    List<Usuario> findByRol(String rol);

    // Para validar en el registro que el email no está ya en uso
    boolean existsByEmail(String email);

    // Búsqueda de usuarios por nombre, útil en el panel de administración
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}