package com.margos.margosbackend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.margos.margosbackend.entity.EstadoInspeccion;
import com.margos.margosbackend.entity.Inspeccion;

public interface InspeccionRepository extends JpaRepository<Inspeccion, Long> {

    Page<Inspeccion> findByEstado(EstadoInspeccion estado, Pageable pageable);

    @EntityGraph(attributePaths = {"respuestas", "adjuntos"})
    Optional<Inspeccion> findOneById(Long id);
}
