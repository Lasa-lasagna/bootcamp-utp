package com.margos.margosbackend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.margos.margosbackend.entity.Sitio;

public interface SitioRepository extends JpaRepository<Sitio, Long> {

    Page<Sitio> findByActivoTrue(Pageable pageable);

    Optional<Sitio> findByNombreIgnoreCase(String nombre);
}
