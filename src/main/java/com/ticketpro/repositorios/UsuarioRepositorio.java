package com.ticketpro.repositorios;

import com.ticketpro.modelos.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    
    Optional<Usuario> findByEmail(String email);

    
    List<Usuario> findByRol(String rol);

    
    boolean existsByEmail(String email);

    
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}