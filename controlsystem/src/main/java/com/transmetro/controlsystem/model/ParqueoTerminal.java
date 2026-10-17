package com.transmetro.controlsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parqueos_patios")
public class ParqueoTerminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombrePatio;

    @Column(nullable = false)
    private Integer libres = 0;

    @Column(nullable = false)
    private Integer totales = 15;

    @Column(nullable = false)
    private Boolean activo = true;

    public ParqueoTerminal() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombrePatio() { return nombrePatio; }
    public void setNombrePatio(String nombrePatio) { this.nombrePatio = nombrePatio; }
    public Integer getLibres() { return libres; }
    public void setLibres(Integer libres) { this.libres = libres; }
    public Integer getTotales() { return totales; }
    public void setTotales(Integer totales) { this.totales = totales; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getEstadoEspacios() {
        return libres + " / " + totales + " Libres";
    }
}