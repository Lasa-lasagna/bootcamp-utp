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
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_plantilla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPlantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    @NotBlank
    @Column(nullable = false, length = 300)
    private String texto;

    @Column(nullable = false)
    private int orden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoRespuesta tipo;
}
