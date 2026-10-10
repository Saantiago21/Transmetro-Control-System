package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.Piloto;
import com.transmetro.controlsystem.repository.PilotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PilotoController {

    @Autowired
    private PilotoRepository pilotoRepository;

    @GetMapping("/pilotos")
    public String listarPilotos(Model model, @RequestParam(name = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("pilotos", pilotoRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrLicenciaContainingIgnoreCase(keyword, keyword, keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("pilotos", pilotoRepository.findAll());
        }
        model.addAttribute("nuevoPiloto", new Piloto());
        return "pilotos";
    }

    @PostMapping("/pilotos/guardar")
    public String guardarPiloto(@ModelAttribute("nuevoPiloto") Piloto piloto) {
        pilotoRepository.save(piloto);
        return "redirect:/pilotos";
    }

    @GetMapping("/pilotos/editar/{id}")
    public String mostrarFormularioEditarPiloto(@PathVariable("id") Long id, Model model) {
        Piloto piloto = pilotoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de piloto inválido:" + id));
        model.addAttribute("nuevoPiloto", piloto);
        model.addAttribute("pilotos", pilotoRepository.findAll());
        return "pilotos";
    }

    @PostMapping("/pilotos/actualizar/{id}")
    public String actualizarPiloto(@PathVariable("id") Long id, @ModelAttribute("nuevoPiloto") Piloto pilotoDetalles) {
        Piloto piloto = pilotoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de piloto inválido:" + id));
        piloto.setNombre(pilotoDetalles.getNombre());
        piloto.setApellido(pilotoDetalles.getApellido());
        piloto.setLicencia(pilotoDetalles.getLicencia());
        piloto.setTelefono(pilotoDetalles.getTelefono());
        piloto.setEstado(pilotoDetalles.getEstado());
        pilotoRepository.save(piloto);
        return "redirect:/pilotos";
    }
}