package com.margos.margosbackend.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.margos.margosbackend.entity.Inspeccion;

public record InspeccionResponse(
    Long id,
    Long sitioId,
    String sitioNombre,
    String inspector,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    Double latitud,
    Double longitud,
    String estado,
    String notas,
    List<RespuestaResponse> respuestas,
    List<AdjuntoResponse> adjuntos
) {

    public static InspeccionResponse from(Inspeccion i) {
        return new InspeccionResponse(
            i.getId(),
            i.getSitio().getId(),
            i.getSitio().getNombre(),
            i.getInspector(),
            i.getFechaInicio(),
            i.getFechaFin(),
            i.getLatitud(),
            i.getLongitud(),
            i.getEstado().name(),
            i.getNotas(),
            i.getRespuestas().stream()
                .sorted(java.util.Comparator.comparing(r -> r.getItem().getOrden()))
                .map(RespuestaResponse::from)
                .toList(),
            i.getAdjuntos().stream().map(AdjuntoResponse::from).toList()
        );
    }
}
