package com.margos.margosbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "respuesta_item",
    uniqueConstraints = @UniqueConstraint(columnNames = {"inspeccion_id", "item_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inspeccion_id", nullable = false)
    private Inspeccion inspeccion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemPlantilla item;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TipoRespuesta valor;

    @Column(length = 1000)
    private String comentario;
}
