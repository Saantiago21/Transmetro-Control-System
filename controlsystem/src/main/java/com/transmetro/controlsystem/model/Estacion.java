package com.transmetro.controlsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "estaciones")
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linea_id")
    private LineaTransmetro linea;

    @Column(name = "nombre_estacion", nullable = false, length = 100)
    private String nombreEstacion;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(name = "estado_afluencia", length = 50)
    private String estadoAfluencia = "Normal";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LineaTransmetro getLinea() { return linea; }
    public void setLinea(LineaTransmetro linea) { this.linea = linea; }
    public String getNombreEstacion() { return nombreEstacion; }
    public void setNombreEstacion(String nombreEstacion) { this.nombreEstacion = nombreEstacion; }
    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }
    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }
    public String getEstadoAfluencia() { return estadoAfluencia; }
    public void setEstadoAfluencia(String estadoAfluencia) { this.estadoAfluencia = estadoAfluencia; }
}