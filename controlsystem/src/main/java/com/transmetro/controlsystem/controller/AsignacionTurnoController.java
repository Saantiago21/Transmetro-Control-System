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
@RequestMapping("/turnos")
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
        model.addAttribute("nuevoTurno", new AsignacionTurno());
        model.addAttribute("buses", busRepository.findAll());
        model.addAttribute("pilotos", pilotoRepository.findAll());
        model.addAttribute("rutas", rutaRepository.findAll());
        return "turnos";
    }

    @PostMapping("/guardar")
    public String guardarTurno(@ModelAttribute("nuevoTurno") AsignacionTurno turno) {
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
    public String actualizarTurno(@PathVariable("id") Long id, @ModelAttribute("nuevoTurno") AsignacionTurno turnoDetalles) {
        AsignacionTurno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de turno inválido:" + id));
        turno.setBus(turnoDetalles.getBus());
        turno.setPiloto(turnoDetalles.getPiloto());
        turno.setRuta(turnoDetalles.getRuta());
        turno.setFechaTurno(turnoDetalles.getFechaTurno());
        turno.setHorario(turnoDetalles.getHorario());
        turno.setEstado(turnoDetalles.getEstado());
        turnoRepository.save(turno);
        return "redirect:/turnos";
    }
}