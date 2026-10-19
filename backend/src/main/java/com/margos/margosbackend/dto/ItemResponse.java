package com.margos.margosbackend.dto;

import com.margos.margosbackend.entity.ItemPlantilla;

public record ItemResponse(Long id, String texto, int orden, String tipo) {

    public static ItemResponse from(ItemPlantilla i) {
        return new ItemResponse(i.getId(), i.getTexto(), i.getOrden(), i.getTipo().name());
    }
}
