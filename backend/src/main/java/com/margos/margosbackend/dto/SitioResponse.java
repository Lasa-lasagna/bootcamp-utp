package com.margos.margosbackend.dto;

import com.margos.margosbackend.entity.Sitio;

public record SitioResponse(Long id, String nombre, String direccion, String contacto, boolean activo) {

    public static SitioResponse from(Sitio s) {
        return new SitioResponse(s.getId(), s.getNombre(), s.getDireccion(), s.getContacto(), s.isActivo());
    }
}
