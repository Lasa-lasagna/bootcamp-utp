package com.margos.margosbackend.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.margos.margosbackend.dto.PlantillaRequest;
import com.margos.margosbackend.dto.PlantillaResponse;
import com.margos.margosbackend.service.PlantillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/plantillas")
@RequiredArgsConstructor 
@PreAuthorize("hasAuthority('APPROLE_Admin')")
public class PlantillaController {

    private final PlantillaService plantillaService;

    @GetMapping
    public ResponseEntity<List<PlantillaResponse>> listar() {
        return ResponseEntity.ok(plantillaService.listar());
    }

    @PostMapping
    public ResponseEntity<PlantillaResponse> crear(@Valid @RequestBody PlantillaRequest plantilla) {
        PlantillaResponse creada = plantillaService.crear(plantilla);
        return ResponseEntity.created(URI.create("/api/plantillas/" + creada.id())).body(creada);
    }
}
