package com.margos.margosbackend.dto;

import com.margos.margosbackend.entity.RespuestaItem;

public record RespuestaResponse(Long itemId, String texto, int orden, String valor, String comentario) {

    public static RespuestaResponse from(RespuestaItem r) {
        return new RespuestaResponse(
            r.getItem().getId(),
            r.getItem().getTexto(),
            r.getItem().getOrden(),
            r.getValor() == null ? null : r.getValor().name(),
            r.getComentario()
        );
    }
}
