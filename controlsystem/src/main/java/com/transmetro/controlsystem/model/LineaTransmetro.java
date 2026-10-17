package com.transmetro.controlsystem.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lineas_transmetro")
public class LineaTransmetro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "codigo_linea", nullable = false, unique = true, length = 20)
    private String codigoLinea;

    @Column(name = "color_hex", length = 7)
    private String colorHex = "#3388ff";

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "linea", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<CoordenadaLinea> coordenadas = new ArrayList<>();

    @OneToMany(mappedBy = "linea", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Estacion> estaciones = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigoLinea() { return codigoLinea; }
    public void setCodigoLinea(String codigoLinea) { this.codigoLinea = codigoLinea; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
    public List<CoordenadaLinea> getCoordenadas() { return coordenadas; }
    public void setCoordenadas(List<CoordenadaLinea> coordenadas) { this.coordenadas = coordenadas; }
    public List<Estacion> getEstaciones() { return estaciones; }
    public void setEstaciones(List<Estacion> estaciones) { this.estaciones = estaciones; }
}