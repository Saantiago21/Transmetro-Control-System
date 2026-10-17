package com.transmetro.controlsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "municipalidades_zonas")
public class MunicipalidadZona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombreZona;

    @Column(name = "unidades", nullable = false)
    private Integer cantidadUnidades = 0;

    @Column(nullable = false)
    private Boolean activo = true;

    public MunicipalidadZona() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreZona() { return nombreZona; }
    public void setNombreZona(String nombreZona) { this.nombreZona = nombreZona; }
    public Integer getCantidadUnidades() { return cantidadUnidades; }
    public void setCantidadUnidades(Integer cantidadUnidades) { this.cantidadUnidades = cantidadUnidades; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}