package com.ticketpro.repositorios;

import com.ticketpro.modelos.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // 🔐 EL SAGRADO LOGIN (Intacto, ni un rasguño)
    Optional<Usuario> findByEmail(String email);

    // =========================================================================
    // 🚀 NUEVOS SUPERPODERES DE USABILIDAD (Sin alterar el login)
    // =========================================================================

    // 1. DESPLEGABLES DE ASIGNACIÓN: Filtra usuarios por su rol (ej: 'INFORMATICO'
    // o 'cliente')
    // ¡Este es VITAL para cargar la lista de técnicos en el desplegable de
    // asignación de tickets!
    List<Usuario> findByRol(String rol);

    // 2. SEGURIDAD EN REGISTRO: Comprueba si un email ya está registrado antes de
    // crearlo
    // Devuelve true o false. Sirve para avisar al usuario: "Este correo ya está en
    // uso" sin romper la app.
    boolean existsByEmail(String email);

    // 3. BUSCADOR DE PERSONAL: Busca usuarios por nombre (coincidencia parcial e
    // ignorando mayúsculas)
    // Ideal para que el administrador busque a un cliente o técnico por su nombre
    // en el panel de control.
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}