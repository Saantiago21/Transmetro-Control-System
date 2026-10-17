package com.transmetro.controlsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "reportes_operativos")
public class ReporteOperativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corredor", nullable = false)
    private String corredor = "Red completa";

    @Column(name = "tipo_incidencia", nullable = false)
    private String tipoIncidencia = "Instantánea operativa";

    @Column(name = "titulo_reporte")
    private String tituloReporte;

    @Column(name = "resumen_estado", length = 1000)
    private String resumenEstado;

    @Column(name = "fecha_hora")
    private String fechaHora;

    public ReporteOperativo() {
        this.fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public ReporteOperativo(String tituloReporte, String resumenEstado) {
        this.tituloReporte = tituloReporte;
        this.resumenEstado = resumenEstado;
        this.fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCorredor() { return corredor; }
    public void setCorredor(String corredor) { this.corredor = corredor; }
    public String getTipoIncidencia() { return tipoIncidencia; }
    public void setTipoIncidencia(String tipoIncidencia) { this.tipoIncidencia = tipoIncidencia; }
    public String getTituloReporte() { return tituloReporte; }
    public void setTituloReporte(String tituloReporte) { this.tituloReporte = tituloReporte; }
    public String getResumenEstado() { return resumenEstado; }
    public void setResumenEstado(String resumenEstado) { this.resumenEstado = resumenEstado; }
    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
}