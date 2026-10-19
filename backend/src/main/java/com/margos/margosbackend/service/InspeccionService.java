package com.margos.margosbackend.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.margos.margosbackend.dto.AdjuntoResponse;
import com.margos.margosbackend.dto.InspeccionRequest;
import com.margos.margosbackend.dto.InspeccionResponse;
import com.margos.margosbackend.dto.RespuestaRequest;
import com.margos.margosbackend.dto.RespuestaResponse;
import com.margos.margosbackend.entity.Adjunto;
import com.margos.margosbackend.entity.EstadoInspeccion;
import com.margos.margosbackend.entity.Inspeccion;
import com.margos.margosbackend.entity.ItemPlantilla;
import com.margos.margosbackend.entity.Plantilla;
import com.margos.margosbackend.entity.RespuestaItem;
import com.margos.margosbackend.entity.Sitio;
import com.margos.margosbackend.entity.TipoRespuesta;
import com.margos.margosbackend.repository.InspeccionRepository;
import com.margos.margosbackend.repository.PlantillaRepository;
import com.margos.margosbackend.repository.SitioRepository;

@Service
@RequiredArgsConstructor
public class InspeccionService {

    private final InspeccionRepository inspeccionRepository;
    private final SitioRepository sitioRepository;
    private final PlantillaRepository plantillaRepository;
    private final BlobStorageService blobStorageService;

    @Transactional(readOnly = true)
    public Page<InspeccionResponse> listar(EstadoInspeccion estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inspeccion> resultados = estado == null
            ? inspeccionRepository.findAll(pageable)
            : inspeccionRepository.findByEstado(estado, pageable);
        return resultados.map(InspeccionResponse::from);
    }

    @Transactional
    public InspeccionResponse crear(InspeccionRequest request, String inspector) {
        Sitio sitio = sitioRepository.findById(request.sitioId())
            .filter(Sitio::isActivo)
            .orElseThrow(() -> new NoSuchElementException("Sitio no encontrado: " + request.sitioId()));

        Plantilla plantilla = plantillaRepository.findById(request.plantillaId())
            .orElseThrow(() -> new NoSuchElementException("Plantilla no encontrada: " + request.plantillaId()));

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setSitio(sitio);
        inspeccion.setInspector(inspector);
        inspeccion.setFechaInicio(LocalDateTime.now());
        inspeccion.setEstado(EstadoInspeccion.BORRADOR);
        inspeccion.setNotas(request.notas());
        inspeccion.setLatitud(request.latitud());
        inspeccion.setLongitud(request.longitud());

        for (ItemPlantilla item : plantilla.getItems()) {
            RespuestaItem respuesta = new RespuestaItem();
            respuesta.setInspeccion(inspeccion);
            respuesta.setItem(item);
            inspeccion.getRespuestas().add(respuesta);
        }

        return InspeccionResponse.from(inspeccionRepository.save(inspeccion));
    }

    @Transactional(readOnly = true)
    public InspeccionResponse obtener(Long id) {
        return InspeccionResponse.from(buscar(id));
    }

    private Inspeccion buscar(Long id) {
        return inspeccionRepository.findOneById(id)
            .orElseThrow(() -> new NoSuchElementException("Inspeccion no encontrada: " + id));
    }

    @Transactional
    public RespuestaResponse responder(Long inspeccionId, Long itemId, RespuestaRequest request) {
        Inspeccion inspeccion = buscar(inspeccionId);
        validarEditable(inspeccion);

        RespuestaItem respuesta = inspeccion.getRespuestas().stream()
            .filter(r -> r.getItem().getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Item no pertenece a la inspeccion: " + itemId));

        respuesta.setValor(parseValor(request.valor()));
        respuesta.setComentario(request.comentario());

        return RespuestaResponse.from(respuesta);
    }

    @Transactional
    public InspeccionResponse enviar(Long id) {
        Inspeccion inspeccion = buscar(id);
        validarEditable(inspeccion);

        boolean pendientes = inspeccion.getRespuestas().stream()
            .anyMatch(r -> r.getValor() == null);
        if (pendientes) {
            throw new IllegalStateException("Hay items sin responder");
        }

        inspeccion.setEstado(EstadoInspeccion.ENVIADA);
        inspeccion.setFechaFin(LocalDateTime.now());
        return InspeccionResponse.from(inspeccionRepository.save(inspeccion));
    }

    @Transactional
    public AdjuntoResponse adjuntar(Long id, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo esta vacio");
        }

        Inspeccion inspeccion = buscar(id);
        validarEditable(inspeccion);

        String url = blobStorageService.subir(archivo, inspeccion.getId());

        Adjunto adjunto = new Adjunto();
        adjunto.setInspeccion(inspeccion);
        adjunto.setNombreArchivo(archivo.getOriginalFilename());
        adjunto.setUrl(url);
        adjunto.setSubidoEn(LocalDateTime.now());

        inspeccion.getAdjuntos().add(adjunto);
        inspeccionRepository.save(inspeccion);

        return AdjuntoResponse.from(adjunto);
    }

    private void validarEditable(Inspeccion inspeccion) {
        if (inspeccion.getEstado() == EstadoInspeccion.ENVIADA) {
            throw new IllegalStateException("La inspeccion ya fue enviada");
        }
    }

    private TipoRespuesta parseValor(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor de respuesta requerido");
        }
        try {
            return TipoRespuesta.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor invalido, use SI, NO o NA");
        }
    }
}
