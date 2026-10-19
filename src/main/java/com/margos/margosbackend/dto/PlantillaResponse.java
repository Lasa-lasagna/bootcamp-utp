package com.margos.margosbackend.dto;

import java.util.List;
import com.margos.margosbackend.entity.Plantilla;

public record PlantillaResponse(Long id, String nombre, List<ItemResponse> items) {

    public static PlantillaResponse from(Plantilla p) {
        return new PlantillaResponse(
            p.getId(),
            p.getNombre(),
            p.getItems().stream().map(ItemResponse::from).toList()
        );
    }
}
