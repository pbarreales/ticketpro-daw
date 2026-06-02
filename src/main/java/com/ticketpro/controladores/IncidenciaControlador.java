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


    // GET /api/incidencias — listado completo, el más reciente primero
    @GetMapping("/api/incidencias")
    public List<Incidencia> listarTodas() {
        return incidenciaRepositorio.findAllByOrderByFechaCreacionDesc();
    }

    // GET /api/incidencias/buscar?titulo=...
    @GetMapping("/api/incidencias/buscar")
    public List<Incidencia> buscarPorTitulo(@RequestParam String titulo) {
        return incidenciaRepositorio.findByTituloContainingIgnoreCase(titulo);
    }

    // GET /api/incidencias/estado?estado=... (valores: Abierto, En Progreso, Resuelto)
    @GetMapping("/api/incidencias/estado")
    public List<Incidencia> filtrarPorEstado(@RequestParam String estado) {
        return incidenciaRepositorio.findByEstado(estado);
    }

    // GET /api/incidencias/cliente?clienteId=... — dashboard del cliente: solo sus tickets
    @GetMapping("/api/incidencias/cliente")
    public List<Incidencia> verMisTicketsComoCliente(@RequestParam Long clienteId) {
        return incidenciaRepositorio.findByClienteId(clienteId);
    }

    // GET /api/incidencias/sin-asignar — bolsa de trabajo para que los técnicos se asignen tickets
    @GetMapping("/api/incidencias/sin-asignar")
    public List<Incidencia> listarSinAsignar() {
        return incidenciaRepositorio.findByInformaticoIdIsNull();
    }

    // --- Escritura (POST / PUT) ---

    // POST /api/incidencias — la fecha y el estado inicial los fija el servidor, no el cliente
    @PostMapping("/api/incidencias")
    public Incidencia crearTicket(@RequestBody Incidencia nuevaIncidencia) {
        nuevaIncidencia.setFechaCreacion(LocalDateTime.now());
        nuevaIncidencia.setEstado("Abierto");
        return incidenciaRepositorio.save(nuevaIncidencia);
    }

    // PUT /api/incidencias/{id}/estado?nuevoEstado=...
    @PutMapping("/api/incidencias/{id}/estado")
    public Incidencia cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        Incidencia ticket = incidenciaRepositorio.findById(id).orElseThrow();
        ticket.setEstado(nuevoEstado);
        return incidenciaRepositorio.save(ticket);
    }

    // PUT /api/incidencias/{id}/asignar?informaticoId=...
    @PutMapping("/api/incidencias/{id}/asignar")
    public Incidencia asignarTecnico(@PathVariable Long id, @RequestParam Long informaticoId) {
        Incidencia ticket = incidenciaRepositorio.findById(id).orElseThrow();
        ticket.setInformaticoId(informaticoId);
        ticket.setEstado("En Progreso"); // Al asignar técnico, el ticket pasa automáticamente a "En Progreso"
        return incidenciaRepositorio.save(ticket);
    }
}