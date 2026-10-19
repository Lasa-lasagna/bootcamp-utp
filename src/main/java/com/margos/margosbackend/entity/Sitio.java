package com.margos.margosbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sitio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sitio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 200)
    private String direccion;

    @Column(length = 150)
    private String contacto;

    @Column(nullable = false)
    private boolean activo = true;
}
