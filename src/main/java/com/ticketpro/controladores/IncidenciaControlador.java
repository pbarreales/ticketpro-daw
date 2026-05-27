package com.ticketpro.controladores;

import com.ticketpro.modelos.Incidencia;
import com.ticketpro.repositorios.IncidenciaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class IncidenciaControlador {

    @Autowired
    private IncidenciaRepositorio incidenciaRepositorio;

    // =========================================================================
    // 📖 ZONA DE LECTURA (GET) - Lo que ya teníamos
    // =========================================================================
    @GetMapping("/api/incidencias")
    public List<Incidencia> listarTodas() {
        return incidenciaRepositorio.findAllByOrderByFechaCreacionDesc();
    }

    @GetMapping("/api/incidencias/buscar")
    public List<Incidencia> buscarPorTitulo(@RequestParam String titulo) {
        return incidenciaRepositorio.findByTituloContainingIgnoreCase(titulo);
    }

    @GetMapping("/api/incidencias/estado")
    public List<Incidencia> filtrarPorEstado(@RequestParam String estado) {
        return incidenciaRepositorio.findByEstado(estado);
    }

    @GetMapping("/api/incidencias/cliente")
    public List<Incidencia> verMisTicketsComoCliente(@RequestParam Long clienteId) {
        return incidenciaRepositorio.findByClienteId(clienteId);
    }

    @GetMapping("/api/incidencias/sin-asignar")
    public List<Incidencia> listarSinAsignar() {
        return incidenciaRepositorio.findByInformaticoIdIsNull();
    }

    // =========================================================================
    // ✍️ ZONA DE ESCRITURA (POST / PUT) - La nueva artillería
    // =========================================================================

    // 🌟 1. CREAR UN TICKET NUEVO (Para el cliente)
    @PostMapping("/api/incidencias")
    public Incidencia crearTicket(@RequestBody Incidencia nuevaIncidencia) {
        // Le inyectamos la fecha y hora exacta del servidor en el momento del clic
        nuevaIncidencia.setFechaCreacion(LocalDateTime.now());
        // Forzamos el estado inicial para que nadie nos cuele un ticket "Resuelto"
        // desde el inicio
        nuevaIncidencia.setEstado("Abierto");

        // Guardamos en MySQL y devolvemos el objeto ya con su ID generado
        return incidenciaRepositorio.save(nuevaIncidencia);
    }

    // 🌟 2. CAMBIAR EL ESTADO DEL TICKET (Para el informático/admin)
    // URL: http://localhost:8080/api/incidencias/5/estado?nuevoEstado=En Progreso
    @PutMapping("/api/incidencias/{id}/estado")
    public Incidencia cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        // Buscamos el ticket por su ID. Si no existe, explotamos con un error genérico
        // (orElseThrow)
        Incidencia ticket = incidenciaRepositorio.findById(id).orElseThrow();

        // Actualizamos solo el estado
        ticket.setEstado(nuevoEstado);

        // Sobreescribimos en la base de datos
        return incidenciaRepositorio.save(ticket);
    }

    // 🌟 3. ASIGNAR UN TÉCNICO AL TICKET (Para la bolsa de trabajo o admin)
    // URL: http://localhost:8080/api/incidencias/5/asignar?informaticoId=2
    @PutMapping("/api/incidencias/{id}/asignar")
    public Incidencia asignarTecnico(@PathVariable Long id, @RequestParam Long informaticoId) {
        Incidencia ticket = incidenciaRepositorio.findById(id).orElseThrow();
        ticket.setInformaticoId(informaticoId);

        // Opcional pero recomendado: Al asignarlo, pasamos el ticket automáticamente a
        // "En Progreso"
        ticket.setEstado("En Progreso");

        return incidenciaRepositorio.save(ticket);
    }
}