package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.MunicipalidadZona;
import com.transmetro.controlsystem.model.ParqueoTerminal;
import com.transmetro.controlsystem.model.ReporteOperativo;
import com.transmetro.controlsystem.repository.LineaTransmetroRepository;
import com.transmetro.controlsystem.repository.MunicipalidadZonaRepository;
import com.transmetro.controlsystem.repository.ParqueoTerminalRepository;
import com.transmetro.controlsystem.repository.ReporteOperativoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private MunicipalidadZonaRepository municipalidadZonaRepository;

    @Autowired
    private ParqueoTerminalRepository parqueoTerminalRepository;

    @Autowired
    private ReporteOperativoRepository reporteOperativoRepository;

    @Autowired
    private LineaTransmetroRepository lineaTransmetroRepository;

    @GetMapping("/")
    @Transactional(readOnly = true)
    public String index(Model model) {
        model.addAttribute("municipalidades", municipalidadZonaRepository.findAll());
        model.addAttribute("parqueos", parqueoTerminalRepository.findAll());
        model.addAttribute("reportes", reporteOperativoRepository.findAll());
        model.addAttribute("lineas", lineaTransmetroRepository.findAllByOrderByCodigoLineaAsc());
        model.addAttribute("monitor", obtenerResumenMonitoreo());
        
        model.addAttribute("nuevaZona", new MunicipalidadZona());
        model.addAttribute("nuevoParqueo", new ParqueoTerminal());
        
        return "index";
    }

    @GetMapping("/api/monitoreo")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> monitoreoEnVivo() {
        return obtenerResumenMonitoreo();
    }

    @PostMapping("/municipalidades/guardar")
    public RedirectView guardarMunicipalidad(@ModelAttribute("nuevaZona") MunicipalidadZona zona) {
        municipalidadZonaRepository.save(zona);
        return new RedirectView("/", true);
    }

    @PostMapping("/parqueos/guardar")
    public RedirectView guardarParqueo(@ModelAttribute("nuevoParqueo") ParqueoTerminal parqueo) {
        parqueoTerminalRepository.save(parqueo);
        return new RedirectView("/", true);
    }

    @PostMapping("/reportes/generar-automatico")
    public RedirectView generarReporteAutomatico() {
        long totalMunis = municipalidadZonaRepository.count();
        long totalParqueos = parqueoTerminalRepository.count();
        
        String resumen = "Red activa con 7 líneas oficiales. Tiempos de espera: 3.5 min. Ocupación general: 78%. Zonas registradas en BD: " + totalMunis + ". Patios/Parqueos registrados: " + totalParqueos + ".";
        
        ReporteOperativo reporte = new ReporteOperativo("Instantánea Operativa EMT - Ciudad de Guatemala", resumen);
        reporteOperativoRepository.save(reporte);
        
        return new RedirectView("/", true);
    }

    private Map<String, Object> obtenerResumenMonitoreo() {
        var lineas = lineaTransmetroRepository.findAllByOrderByCodigoLineaAsc();
        var municipalidades = municipalidadZonaRepository.findAll();
        var parqueos = parqueoTerminalRepository.findAll();

        long lineasActivas = lineas.stream().filter(linea -> Boolean.TRUE.equals(linea.getActivo())).count();
        long zonasActivas = municipalidades.stream().filter(zona -> Boolean.TRUE.equals(zona.getActivo())).count();
        int unidadesAsignadas = municipalidades.stream()
                .filter(zona -> Boolean.TRUE.equals(zona.getActivo()))
                .mapToInt(zona -> zona.getCantidadUnidades() == null ? 0 : zona.getCantidadUnidades())
                .sum();

        var parqueosActivos = parqueos.stream()
                .filter(parqueo -> Boolean.TRUE.equals(parqueo.getActivo()))
                .toList();
        int plazasLibres = parqueosActivos.stream()
                .mapToInt(parqueo -> parqueo.getLibres() == null ? 0 : parqueo.getLibres())
                .sum();
        int plazasTotales = parqueosActivos.stream()
                .mapToInt(parqueo -> parqueo.getTotales() == null ? 0 : parqueo.getTotales())
                .sum();
        int ocupacionParqueos = plazasTotales == 0 ? 0 : Math.min(100,
                (int) Math.round((double) (plazasTotales - plazasLibres) * 100 / plazasTotales));

        List<Map<String, String>> alertas = new ArrayList<>();
        lineas.stream()
                .filter(linea -> !Boolean.TRUE.equals(linea.getActivo()))
                .forEach(linea -> alertas.add(alerta("warning", "Línea fuera de servicio",
                        linea.getCodigoLinea() + " · " + linea.getNombre() + " está marcada como inactiva.",
                        "fa-route")));

        parqueos.stream()
                .filter(parqueo -> !Boolean.TRUE.equals(parqueo.getActivo()))
                .forEach(parqueo -> alertas.add(alerta("info", "Parqueo inactivo",
                        parqueo.getNombrePatio() + " no está disponible para operación.", "fa-warehouse")));

        if (parqueosActivos.isEmpty()) {
            alertas.add(alerta("warning", "Sin parqueos activos", "No hay parqueos disponibles registrados.", "fa-warehouse"));
        } else {
            for (var parqueo : parqueosActivos) {
                int libres = parqueo.getLibres() == null ? 0 : parqueo.getLibres();
                int total = parqueo.getTotales() == null ? 0 : parqueo.getTotales();
                if (total > 0 && libres <= 0) {
                    alertas.add(alerta("danger", "Capacidad agotada", parqueo.getNombrePatio() + " no tiene espacios libres.",
                            "fa-triangle-exclamation"));
                } else if (total > 0 && (double) libres / total <= 0.2) {
                    alertas.add(alerta("warning", "Disponibilidad baja",
                            parqueo.getNombrePatio() + " tiene " + libres + " de " + total + " espacios libres.",
                            "fa-triangle-exclamation"));
                }
            }
        }

        if (alertas.isEmpty()) {
            alertas.add(alerta("success", "Operación estable", "No hay alertas con los datos registrados.", "fa-circle-check"));
        }

        return Map.of(
                "lineasActivas", lineasActivas,
                "lineasTotales", lineas.size(),
                "zonasActivas", zonasActivas,
                "unidadesAsignadas", unidadesAsignadas,
                "parqueosActivos", parqueosActivos.size(),
                "plazasLibres", plazasLibres,
                "plazasTotales", plazasTotales,
                "ocupacionParqueos", ocupacionParqueos,
                "alertas", alertas
        );
    }

    private Map<String, String> alerta(String nivel, String titulo, String detalle, String icono) {
        return Map.of("nivel", nivel, "titulo", titulo, "detalle", detalle, "icono", icono);
    }
}