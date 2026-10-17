package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.LineaTransmetro;
import com.transmetro.controlsystem.repository.LineaTransmetroRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class LineaMapaController {

    private final LineaTransmetroRepository lineaRepository;

    public LineaMapaController(LineaTransmetroRepository lineaRepository) {
        this.lineaRepository = lineaRepository;
    }

    @GetMapping("/api/lineas")
    @Transactional(readOnly = true)
    public List<LineaMapa> listarLineas() {
        return lineaRepository.findAllByActivoTrueOrderByCodigoLineaAsc().stream()
                .map(linea -> new LineaMapa(
                        linea.getId(),
                        linea.getCodigoLinea(),
                        linea.getNombre(),
                        linea.getColorHex(),
                        linea.getCoordenadas().stream()
                                .map(coordenada -> new Punto(coordenada.getLatitud(), coordenada.getLongitud()))
                                .toList()))
                .toList();
    }

    public record LineaMapa(Long id, String codigo, String nombre, String color, List<Punto> puntos) {}
    public record Punto(BigDecimal latitud, BigDecimal longitud) {}
}