package com.ticketpro.repositorios;

import com.ticketpro.modelos.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // HUECO 1: Le dice a Spring que esto maneja la base de datos
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // HUECO 3: El Query Method mágico para el login
    Optional<Usuario> findByEmail(String email); // Con el Optional encapsulamos a Usuarios por si nos dan un usuario
                                                 // "inventado" o que no existe
                                                 // Para que no te devuelva un null que te "rompa" el programa, te
                                                 // devuelve
                                                 // un envoltorio vacío facil de controlar
}