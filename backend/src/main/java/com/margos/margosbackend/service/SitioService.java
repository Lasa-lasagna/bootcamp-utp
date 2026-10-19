package com.margos.margosbackend.service;

import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.margos.margosbackend.dto.SitioRequest;
import com.margos.margosbackend.dto.SitioResponse;
import com.margos.margosbackend.entity.Sitio;
import com.margos.margosbackend.repository.SitioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SitioService {

    private final SitioRepository sitioRepository;

    @Transactional(readOnly = true)
    public Page<SitioResponse> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sitioRepository.findByActivoTrue(pageable).map(SitioResponse::from);
    }

    @Transactional(readOnly = true)
    public SitioResponse obtener(Long id) {
        return SitioResponse.from(buscar(id));
    }

    @Transactional
    public SitioResponse crear(SitioRequest request) {
        Sitio sitio = new Sitio();
        sitio.setNombre(request.nombre());
        sitio.setDireccion(request.direccion());
        sitio.setContacto(request.contacto());
        sitio.setActivo(request.activo() == null || request.activo());
        return SitioResponse.from(sitioRepository.save(sitio));
    }

    @Transactional
    public SitioResponse actualizar(Long id, SitioRequest request) {
        Sitio sitio = buscar(id);
        sitio.setNombre(request.nombre());
        sitio.setDireccion(request.direccion());
        sitio.setContacto(request.contacto());
        if (request.activo() != null) {
            sitio.setActivo(request.activo());
        }
        return SitioResponse.from(sitioRepository.save(sitio));
    }

    @Transactional
    public void eliminar(Long id) {
        Sitio sitio = buscar(id);
        sitio.setActivo(false);
        sitioRepository.save(sitio);
    }

    private Sitio buscar(Long id) {
        return sitioRepository.findById(id)
            .filter(Sitio::isActivo)
            .orElseThrow(() -> new NoSuchElementException("Sitio no encontrado: " + id));
    }
}
