package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.CoordenadaLinea;
import com.transmetro.controlsystem.model.Estacion;
import com.transmetro.controlsystem.model.LineaTransmetro;
import com.transmetro.controlsystem.repository.EstacionRepository;
import com.transmetro.controlsystem.repository.LineaTransmetroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LineaTransmetroController {

    private final LineaTransmetroRepository lineaRepository;
    private final EstacionRepository estacionRepository;

    public LineaTransmetroController(LineaTransmetroRepository lineaRepository,
                                     EstacionRepository estacionRepository) {
        this.lineaRepository = lineaRepository;
        this.estacionRepository = estacionRepository;
    }

    @PostMapping("/lineas/guardar")
    public String guardarLinea(@RequestParam String nombre,
                               @RequestParam String codigoLinea,
                               @RequestParam(defaultValue = "#3388ff") String colorHex,
                               @RequestParam(required = false) String descripcion,
                               RedirectAttributes redirectAttributes) {
        String codigoNormalizado = codigoLinea.trim().toUpperCase();
        if (lineaRepository.existsByCodigoLinea(codigoNormalizado)) {
            redirectAttributes.addFlashAttribute("errorLineas", "Ya existe una línea con ese código.");
            return "redirect:/rutas";
        }
        if (!colorValido(colorHex)) {
            redirectAttributes.addFlashAttribute("errorLineas", "El color debe tener formato hexadecimal #RRGGBB.");
            return "redirect:/rutas";
        }

        LineaTransmetro linea = new LineaTransmetro();
        linea.setNombre(nombre.trim());
        linea.setCodigoLinea(codigoNormalizado);
        linea.setColorHex(colorHex);
        linea.setDescripcion(descripcion);
        linea.setActivo(true);
        lineaRepository.save(linea);
        redirectAttributes.addFlashAttribute("mensajeLineas", "Línea agregada.");
        return "redirect:/rutas";
    }

    @PostMapping("/lineas/{id}/actualizar")
    @Transactional
    public String actualizarLinea(@PathVariable Long id,
                                  @RequestParam String nombre,
                                  @RequestParam String codigoLinea,
                                  @RequestParam String colorHex,
                                  @RequestParam(required = false) String descripcion,
                                  @RequestParam(defaultValue = "false") boolean activo,
                                  RedirectAttributes redirectAttributes) {
        LineaTransmetro linea = lineaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Línea no encontrada: " + id));
        String codigoNormalizado = codigoLinea.trim().toUpperCase();
        boolean codigoEnUso = lineaRepository.existsByCodigoLinea(codigoNormalizado)
                && !linea.getCodigoLinea().equalsIgnoreCase(codigoNormalizado);
        if (codigoEnUso) {
            redirectAttributes.addFlashAttribute("errorLineas", "Ya existe una línea con ese código.");
            return "redirect:/rutas";
        }
        if (!colorValido(colorHex)) {
            redirectAttributes.addFlashAttribute("errorLineas", "El color debe tener formato hexadecimal #RRGGBB.");
            return "redirect:/rutas";
        }

        linea.setNombre(nombre.trim());
        linea.setCodigoLinea(codigoNormalizado);
        linea.setColorHex(colorHex);
        linea.setDescripcion(descripcion);
        linea.setActivo(activo);
        redirectAttributes.addFlashAttribute("mensajeLineas", "Línea actualizada.");
        return "redirect:/rutas";
    }

    @PostMapping("/lineas/{id}/coordenadas")
    @Transactional
    public String guardarCoordenadas(@PathVariable Long id,
                                     @RequestParam String puntos,
                                     RedirectAttributes redirectAttributes) {
        LineaTransmetro linea = lineaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Línea no encontrada: " + id));
        List<CoordenadaLinea> coordenadas;
        try {
            coordenadas = parsearCoordenadas(puntos, linea);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorLineas", exception.getMessage());
            return "redirect:/rutas";
        }

        linea.getCoordenadas().clear();
        linea.getCoordenadas().addAll(coordenadas);
        lineaRepository.save(linea);
        redirectAttributes.addFlashAttribute("mensajeLineas", "Trazado guardado para " + linea.getCodigoLinea() + ".");
        return "redirect:/rutas";
    }

    @PostMapping("/lineas/{id}/estaciones")
    public String agregarEstacion(@PathVariable Long id,
                                  @RequestParam String nombreEstacion,
                                  @RequestParam BigDecimal latitud,
                                  @RequestParam BigDecimal longitud,
                                  @RequestParam(defaultValue = "Normal") String estadoAfluencia,
                                  RedirectAttributes redirectAttributes) {
        if (!coordenadasValidas(latitud, longitud)) {
            redirectAttributes.addFlashAttribute("errorLineas", "Latitud o longitud fuera de rango.");
            return "redirect:/rutas";
        }
        LineaTransmetro linea = lineaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Línea no encontrada: " + id));
        Estacion estacion = new Estacion();
        estacion.setLinea(linea);
        estacion.setNombreEstacion(nombreEstacion.trim());
        estacion.setLatitud(latitud);
        estacion.setLongitud(longitud);
        estacion.setEstadoAfluencia(estadoAfluencia);
        estacionRepository.save(estacion);
        redirectAttributes.addFlashAttribute("mensajeLineas", "Estación agregada a " + linea.getCodigoLinea() + ".");
        return "redirect:/rutas";
    }

    @PostMapping("/estaciones/{id}/actualizar")
    public String actualizarEstacion(@PathVariable Long id,
                                     @RequestParam String nombreEstacion,
                                     @RequestParam BigDecimal latitud,
                                     @RequestParam BigDecimal longitud,
                                     @RequestParam(defaultValue = "Normal") String estadoAfluencia,
                                     RedirectAttributes redirectAttributes) {
        Estacion estacion = estacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada: " + id));
        if (!coordenadasValidas(latitud, longitud)) {
            redirectAttributes.addFlashAttribute("errorLineas", "Latitud o longitud fuera de rango.");
            return "redirect:/rutas";
        }
        estacion.setNombreEstacion(nombreEstacion.trim());
        estacion.setLatitud(latitud);
        estacion.setLongitud(longitud);
        estacion.setEstadoAfluencia(estadoAfluencia);
        estacionRepository.save(estacion);
        redirectAttributes.addFlashAttribute("mensajeLineas", "Estación actualizada.");
        return "redirect:/rutas";
    }

    private List<CoordenadaLinea> parsearCoordenadas(String puntos, LineaTransmetro linea) {
        List<CoordenadaLinea> coordenadas = new ArrayList<>();
        int orden = 1;
        for (String fila : puntos.split("\\R")) {
            if (fila.isBlank()) {
                continue;
            }
            String[] valores = fila.trim().split("[,;\\s]+", -1);
            if (valores.length != 2) {
                throw new IllegalArgumentException("Usa una latitud y longitud por línea, separadas por coma.");
            }
            BigDecimal latitud;
            BigDecimal longitud;
            try {
                latitud = new BigDecimal(valores[0]);
                longitud = new BigDecimal(valores[1]);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Hay coordenadas con formato numérico inválido.");
            }
            if (!coordenadasValidas(latitud, longitud)) {
                throw new IllegalArgumentException("Latitud o longitud fuera de rango.");
            }
            CoordenadaLinea coordenada = new CoordenadaLinea();
            coordenada.setLinea(linea);
            coordenada.setLatitud(latitud);
            coordenada.setLongitud(longitud);
            coordenada.setOrden(orden++);
            coordenadas.add(coordenada);
        }
        if (!puntos.isBlank() && coordenadas.size() < 2) {
            throw new IllegalArgumentException("Cada trazado necesita al menos dos puntos.");
        }
        return coordenadas;
    }

    private boolean colorValido(String colorHex) {
        return colorHex != null && colorHex.matches("#[0-9a-fA-F]{6}");
    }

    private boolean coordenadasValidas(BigDecimal latitud, BigDecimal longitud) {
        return latitud.compareTo(BigDecimal.valueOf(-90)) >= 0
                && latitud.compareTo(BigDecimal.valueOf(90)) <= 0
                && longitud.compareTo(BigDecimal.valueOf(-180)) >= 0
                && longitud.compareTo(BigDecimal.valueOf(180)) <= 0;
    }
}