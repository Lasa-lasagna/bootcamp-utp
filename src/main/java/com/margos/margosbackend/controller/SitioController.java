package com.margos.margosbackend.controller;

import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.margos.margosbackend.dto.SitioRequest;
import com.margos.margosbackend.dto.SitioResponse;
import com.margos.margosbackend.service.SitioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sitios")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAuthority('APPROLE_Admin')")
public class SitioController {

    private final SitioService sitioService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<SitioResponse>> listar(
        @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
        @Positive @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(sitioService.listar(page, size));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SitioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(sitioService.obtener(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SitioResponse> crear(@Valid @RequestBody SitioRequest sitio) {
        SitioResponse creado = sitioService.crear(sitio);
        return ResponseEntity.created(URI.create("/api/sitios/" + creado.id())).body(creado);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<SitioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody SitioRequest sitio) {
        return ResponseEntity.ok(sitioService.actualizar(id, sitio));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sitioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
