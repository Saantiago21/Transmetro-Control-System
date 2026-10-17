package com.transmetro.controlsystem;

import com.transmetro.controlsystem.model.Ruta;
import com.transmetro.controlsystem.model.LineaTransmetro;
import com.transmetro.controlsystem.repository.AsignacionTurnoRepository;
import com.transmetro.controlsystem.repository.LineaTransmetroRepository;
import com.transmetro.controlsystem.repository.RutaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {

    private final RutaRepository rutaRepository;
    private final LineaTransmetroRepository lineaRepository;
    private final AsignacionTurnoRepository turnoRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataLoader(RutaRepository rutaRepository,
                      LineaTransmetroRepository lineaRepository,
                      AsignacionTurnoRepository turnoRepository,
                      JdbcTemplate jdbcTemplate) {
        this.rutaRepository = rutaRepository;
        this.lineaRepository = lineaRepository;
        this.turnoRepository = turnoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Ruta> rutasOficiales = List.of(
                crearRuta("L1", "Línea 1", "San Sebastián - Centro Cívico (Zona 1)"),
                crearRuta("L2", "Línea 2", "San Sebastián - Hipódromo del Norte (Zona 2)"),
                crearRuta("L6", "Línea 6", "FEGUA - Proyectos (Zona 6)"),
                crearRuta("L7", "Línea 7", "USAC Periférico - La Merced (Zonas 7 y 11)"),
                crearRuta("L12", "Línea 12", "Centra Sur - Plaza Barrios (Zona 12)"),
                crearRuta("L13", "Línea 13", "Tipografía - Plaza Argentina (Zona 13)"),
                crearRuta("L18", "Línea 18", "FEGUA - Centra Atlántida (Zona 18)")
        );
        Set<String> codigosAntiguos = Set.of("L-1", "L-2", "L-3", "L-4");

            for (Ruta rutaExistente : rutaRepository.findAll()) {
            if (codigosAntiguos.contains(rutaExistente.getCodigoRuta())) {
                eliminarDatosDependientes(rutaExistente.getIdRuta());
                turnoRepository.deleteAll(turnoRepository.findAllByRuta_IdRuta(rutaExistente.getIdRuta()));
                rutaRepository.delete(rutaExistente);
                }
            }

        List<Ruta> rutasFaltantes = rutasOficiales.stream()
                .filter(ruta -> !rutaRepository.existsByCodigoRuta(ruta.getCodigoRuta()))
                .toList();
        if (!rutasFaltantes.isEmpty()) {
            rutaRepository.saveAll(rutasFaltantes);
        }

        List<LineaTransmetro> lineasOficiales = List.of(
            crearLinea("L1", "Línea 1 · San Sebastián - Centro Cívico", "#6bc026",
                "San Sebastián - Centro Cívico. Estaciones: Gómez Carrillo, San Agustín, Parque Centenario, Mercado Central."),
            crearLinea("L2", "Línea 2 · San Sebastián - Hipódromo del Norte", "#8b5cf6",
                "San Sebastián - Hipódromo del Norte. Estaciones: Jocotenango, San Simeón, Simeón Cañas, Parque Morazán."),
            crearLinea("L6", "Línea 6 · FEGUA - Proyectos Zona 6", "#eab308",
                "FEGUA - Proyectos Zona 6. Estaciones: Cerro del Carmen, Parroquia, Victorias, San Martín."),
            crearLinea("L7", "Línea 7 · USAC Periférico - La Merced", "#64748b",
                "USAC Periférico - La Merced. Estaciones: San Juan, Roosevelt, Cejusa, Charcas, USAC Periférico."),
            crearLinea("L12", "Línea 12 · Centra Sur - Plaza Barrios", "#f97316",
                "Centra Sur - Plaza Barrios. Estaciones: Trébol, El Carmen, Reformador, España, CENMA."),
            crearLinea("L13", "Línea 13 · Tipografía - Plaza Argentina", "#22c55e",
                "Tipografía - Plaza Argentina. Estaciones: Hangares, Fuerza Aérea, Plaza Argentina, Torre del Reformador."),
            crearLinea("L18", "Línea 18 · FEGUA - Centro Atlántida", "#06b6d4",
                "FEGUA - Centro Atlántida.")
        );

        for (LineaTransmetro oficial : lineasOficiales) {
            lineaRepository.findByCodigoLinea(oficial.getCodigoLinea()).ifPresentOrElse(existente -> {
            if (existente.getColorHex() == null || existente.getColorHex().isBlank()) {
                existente.setColorHex(oficial.getColorHex());
            }
                if (existente.getDescripcion() == null || existente.getDescripcion().isBlank()) {
                    existente.setDescripcion(oficial.getDescripcion());
                }
                if (existente.getNombre() == null || existente.getNombre().isBlank()) {
                    existente.setNombre(oficial.getNombre());
                }
                lineaRepository.save(existente);
            }, () -> lineaRepository.save(oficial));
        }
    }

    private void eliminarDatosDependientes(Long idRuta) {
        Boolean existeTablaMonitoreo = jdbcTemplate.queryForObject(
                "SELECT to_regclass('public.monitoreo_frecuencia') IS NOT NULL", Boolean.class);
        if (Boolean.TRUE.equals(existeTablaMonitoreo)) {
            jdbcTemplate.update("""
                    DELETE FROM monitoreo_frecuencia
                    WHERE id_asignacion IN (
                        SELECT id_asignacion FROM asignacion_turnos WHERE id_ruta = ?
                    )
                    """, idRuta);
        }
    }

    private Ruta crearRuta(String codigo, String nombre, String descripcion) {
        Ruta ruta = new Ruta();
        ruta.setCodigoRuta(codigo);
        ruta.setNombreRuta(nombre);
        ruta.setDescripcion(descripcion);
        ruta.setEstado("Activa");
        return ruta;
    }

    private LineaTransmetro crearLinea(String codigo, String nombre, String color, String descripcion) {
        LineaTransmetro linea = new LineaTransmetro();
        linea.setCodigoLinea(codigo);
        linea.setNombre(nombre);
        linea.setColorHex(color);
        linea.setDescripcion(descripcion);
        linea.setActivo(true);
        return linea;
    }
}