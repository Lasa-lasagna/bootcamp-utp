package com.margos.margosbackend.dto;

import java.time.LocalDateTime;
import com.margos.margosbackend.entity.Adjunto;

public record AdjuntoResponse(Long id, String nombreArchivo, String url, LocalDateTime subidoEn) {

    public static AdjuntoResponse from(Adjunto a) {
        return new AdjuntoResponse(a.getId(), a.getNombreArchivo(), a.getUrl(), a.getSubidoEn());
    }
}
