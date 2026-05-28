package com.ticketpro.repositorios;

import com.ticketpro.modelos.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidenciaRepositorio extends JpaRepository<Incidencia, Long> {

    // --- Búsqueda por texto y estado ---
    List<Incidencia> findByTituloContainingIgnoreCase(String titulo);
    List<Incidencia> findByEstado(String estado);
    List<Incidencia> findByCategoriaId(Long categoriaId);

    // --- Filtros por rol: usados en los dashboards de cliente e informático ---
    List<Incidencia> findByClienteId(Long clienteId);
    List<Incidencia> findByInformaticoId(Long informaticoId);
    // Incidencias sin técnico asignado (bolsa de trabajo)
    List<Incidencia> findByInformaticoIdIsNull();

    // --- Filtros combinados ---
    List<Incidencia> findByClienteIdAndEstado(Long clienteId, String estado);
    List<Incidencia> findByInformaticoIdAndEstado(Long informaticoId, String estado);

    // Listado completo ordenado de más reciente a más antiguo
    List<Incidencia> findAllByOrderByFechaCreacionDesc();
}