package com.margos.margosbackend.controller;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.margos.margosbackend.dto.AdjuntoResponse;
import com.margos.margosbackend.dto.InspeccionRequest;
import com.margos.margosbackend.dto.InspeccionResponse;
import com.margos.margosbackend.dto.RespuestaRequest;
import com.margos.margosbackend.dto.RespuestaResponse;
import com.margos.margosbackend.entity.EstadoInspeccion;
import com.margos.margosbackend.service.InspeccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inspecciones")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('APPROLE_Admin')")
public class InspeccionController {

    private final InspeccionService inspeccionService;

    @PostMapping
    public ResponseEntity<InspeccionResponse> crear(
        @Valid @RequestBody InspeccionRequest request,
        Authentication authentication
    ) {
        InspeccionResponse creada = inspeccionService.crear(request, authentication.getName());
        return ResponseEntity.ok(creada);
    }

    @GetMapping
    public ResponseEntity<Page<InspeccionResponse>> listar(
        @RequestParam(required = false) EstadoInspeccion estado,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(inspeccionService.listar(estado, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspeccionResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(inspeccionService.obtener(id));
    }

    @PatchMapping("/{id}/items/{itemId}/respuesta")
    public ResponseEntity<RespuestaResponse> responder(
        @PathVariable Long id,
        @PathVariable Long itemId,
        @Valid @RequestBody RespuestaRequest request
    ) {
        return ResponseEntity.ok(inspeccionService.responder(id, itemId, request));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<InspeccionResponse> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(inspeccionService.enviar(id));
    }

    @PostMapping(value = "/{id}/adjuntos", consumes = "multipart/form-data")
    public ResponseEntity<AdjuntoResponse> adjuntar(
        @PathVariable Long id,
        @RequestPart("archivo") MultipartFile archivo
    ) {
        return ResponseEntity.ok(inspeccionService.adjuntar(id, archivo));
    }
}
