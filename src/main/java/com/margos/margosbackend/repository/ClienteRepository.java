package com.margos.margosbackend.repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.margos.margosbackend.entity.Cliente;


public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    Optional<Cliente> findByEmail(String email);
    Page<Cliente> findByActivoTrue(Pageable pageable);
}
