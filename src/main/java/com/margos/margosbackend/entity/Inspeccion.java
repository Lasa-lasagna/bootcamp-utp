package com.margos.margosbackend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inspeccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sitio_id", nullable = false)
    private Sitio sitio;

    @Column(nullable = false, length = 200)
    private String inspector;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column
    private LocalDateTime fechaFin;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInspeccion estado = EstadoInspeccion.BORRADOR;

    @Column(length = 2000)
    private String notas;

    @OneToMany(mappedBy = "inspeccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaItem> respuestas = new ArrayList<>();

    @OneToMany(mappedBy = "inspeccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Adjunto> adjuntos = new ArrayList<>();
}
