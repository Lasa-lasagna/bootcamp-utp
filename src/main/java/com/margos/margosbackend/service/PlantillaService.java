package com.margos.margosbackend.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.margos.margosbackend.dto.PlantillaResponse;
import com.margos.margosbackend.dto.PlantillaRequest;
import com.margos.margosbackend.entity.ItemPlantilla;
import com.margos.margosbackend.entity.Plantilla;
import com.margos.margosbackend.entity.TipoRespuesta;
import com.margos.margosbackend.repository.PlantillaRepository;

@Service
@RequiredArgsConstructor
public class PlantillaService {

    private final PlantillaRepository plantillaRepository;

    @Transactional(readOnly = true)
    public List<PlantillaResponse> listar() {
        return plantillaRepository.findAllByOrderByNombreAsc().stream()
            .map(PlantillaResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public Plantilla obtener(Long id) {
        return plantillaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Plantilla no encontrada: " + id));
    }

    @Transactional
    public PlantillaResponse crear(PlantillaRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("La plantilla debe tener al menos un item");
        }

        Plantilla plantilla = new Plantilla();
        plantilla.setNombre(request.nombre());

        for (PlantillaRequest.ItemRequest itemRequest : request.items()) {
            ItemPlantilla item = new ItemPlantilla();
            item.setPlantilla(plantilla);
            item.setTexto(itemRequest.texto());
            item.setOrden(itemRequest.orden());
            item.setTipo(parseTipo(itemRequest.tipo()));
            plantilla.getItems().add(item);
        }

        return PlantillaResponse.from(plantillaRepository.save(plantilla));
    }

    private TipoRespuesta parseTipo(String tipo) {
        try {
            return TipoRespuesta.valueOf(tipo == null ? "SI" : tipo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de respuesta invalido: " + tipo);
        }
    }
}
