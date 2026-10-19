package com.margos.margosbackend.dto;

import java.util.List;

public record PlantillaRequest(String nombre, List<ItemRequest> items) {

    public record ItemRequest(String texto, int orden, String tipo) {
    }
}
