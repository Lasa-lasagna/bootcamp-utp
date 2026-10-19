package com.margos.margosbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import com.margos.margosbackend.entity.Plantilla;

public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

    @EntityGraph(attributePaths = "items")
    List<Plantilla> findAllByOrderByNombreAsc();
}
