package com.margos.margosbackend.dto;

public record InspeccionRequest(Long sitioId, Long plantillaId, String notas, Double latitud, Double longitud) {
}
