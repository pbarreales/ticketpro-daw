package com.ticketpro.repositorios;

import com.ticketpro.modelos.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidenciaRepositorio extends JpaRepository<Incidencia, Long> {

    // =========================================================================
    // 🔍 1. FILTROS BÁSICOS Y BUSCADORES DE TEXTO
    // =========================================================================

    // El buscador por título inteligente que ya teníamos
    List<Incidencia> findByTituloContainingIgnoreCase(String titulo);

    // Buscar directamente por el Estado de la incidencia (ej: "Pendiente", "En
    // Proceso", "Resuelta")
    List<Incidencia> findByEstado(String estado);

    // Buscar por Categoría (Para filtrar tickets por "Redes", "Hardware",
    // "Software")
    List<Incidencia> findByCategoriaId(Long categoriaId);

    // =========================================================================
    // 👤 2. FILTROS DE ROL (Fundamentales para los Dashboards de los usuarios)
    // =========================================================================

    // Panel del Cliente: Muestra únicamente los tickets que ha creado ese cliente
    // específico
    List<Incidencia> findByClienteId(Long clienteId);

    // Panel del Informático: Muestra los tickets que tiene asignados ese técnico
    // específico
    List<Incidencia> findByInformaticoId(Long informaticoId);

    // Bolsa de Trabajo: Muestra los tickets que NO tienen ningún informático
    // asignado aún (informatico_id IS NULL)
    // ¡Este es clave para que los técnicos puedan "pescar" y asignarse incidencias
    // pendientes!
    List<Incidencia> findByInformaticoIdIsNull();

    // =========================================================================
    // 🎛️ 3. FILTROS COMBINADOS AVANZADOS (Máxima Usabilidad en UI)
    // =========================================================================

    // Mis Tickets Activos (Cliente): Filtra los tickets de un cliente que además
    // están en un estado concreto
    List<Incidencia> findByClienteIdAndEstado(Long clienteId, String estado);

    // Mis Tareas Activas (Informático): Filtra los tickets de un técnico que están
    // en proceso
    List<Incidencia> findByInformaticoIdAndEstado(Long informaticoId, String estado);

    // =========================================================================
    // ⏱️ 4. ORDENACIÓN CRONOLÓGICA (Para que lo más nuevo salga arriba de la tabla)
    // =========================================================================

    // Traer absolutamente todas las incidencias ordenadas de la más reciente a la
    // más antigua
    List<Incidencia> findAllByOrderByFechaCreacionDesc();
}