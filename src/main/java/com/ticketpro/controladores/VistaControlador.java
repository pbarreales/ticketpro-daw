package com.ticketpro.controladores; // Asegúrate de que coincida con tu paquete

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaControlador {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Spring Boot buscará login.html en la carpeta templates
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro"; // Spring Boot buscará registro.html en la carpeta templates
    }
}