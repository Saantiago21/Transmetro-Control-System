package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.AsignacionTurno;
import com.transmetro.controlsystem.repository.AsignacionTurnoRepository;
import com.transmetro.controlsystem.repository.BusRepository;
import com.transmetro.controlsystem.repository.PilotoRepository;
import com.transmetro.controlsystem.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/turnos", "/asignacion-turnos"})
public class AsignacionTurnoController {

    @Autowired
    private AsignacionTurnoRepository turnoRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private PilotoRepository pilotoRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @GetMapping
    public String listarTurnos(Model model) {
        model.addAttribute("turnos", turnoRepository.findAll());
        AsignacionTurno nuevoTurno = new AsignacionTurno();
        nuevoTurno.setEstado("Programado");
        model.addAttribute("nuevoTurno", nuevoTurno);
        model.addAttribute("buses", busRepository.findAll());
        model.addAttribute("pilotos", pilotoRepository.findAll());
        model.addAttribute("rutas", rutaRepository.findAll());
        return "turnos";
    }

    @PostMapping("/guardar")
    public String guardarTurno(@ModelAttribute("nuevoTurno") AsignacionTurno turno,
                                 @RequestParam Long busId,
                                 @RequestParam Long pilotoId,
                                 @RequestParam Long rutaId,
                                 @RequestParam String horario) {
        asignarReferencias(turno, busId, pilotoId, rutaId);
        turno.setHorario(horario);
        turnoRepository.save(turno);
        return "redirect:/turnos";
    }

    @GetMapping("/editar/{id}")
    public String editarTurno(@PathVariable("id") Long id, Model model) {
        AsignacionTurno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de turno inválido:" + id));
        model.addAttribute("nuevoTurno", turno);
        model.addAttribute("turnos", turnoRepository.findAll());
        model.addAttribute("buses", busRepository.findAll());
        model.addAttribute("pilotos", pilotoRepository.findAll());
        model.addAttribute("rutas", rutaRepository.findAll());
        return "turnos";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarTurno(@PathVariable("id") Long id,
                                  @ModelAttribute("nuevoTurno") AsignacionTurno turnoDetalles,
                                  @RequestParam Long busId,
                                  @RequestParam Long pilotoId,
                                  @RequestParam Long rutaId,
                                  @RequestParam String horario) {
        AsignacionTurno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de turno inválido:" + id));
        asignarReferencias(turno, busId, pilotoId, rutaId);
        turno.setFechaTurno(turnoDetalles.getFechaTurno());
        turno.setHorario(horario);
        turno.setEstado(turnoDetalles.getEstado());
        turnoRepository.save(turno);
        return "redirect:/turnos";
    }

    private void asignarReferencias(AsignacionTurno turno, Long busId, Long pilotoId, Long rutaId) {
        turno.setBus(busRepository.findById(busId)
            .orElseThrow(() -> new IllegalArgumentException("ID de bus inválido: " + busId)));
        turno.setPiloto(pilotoRepository.findById(pilotoId)
            .orElseThrow(() -> new IllegalArgumentException("ID de piloto inválido: " + pilotoId)));
        turno.setRuta(rutaRepository.findById(rutaId)
            .orElseThrow(() -> new IllegalArgumentException("ID de ruta inválido: " + rutaId)));
    }
}