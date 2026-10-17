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
        Piloto nuevoPiloto = new Piloto();
        nuevoPiloto.setEstado("Activo");
        model.addAttribute("nuevoPiloto", nuevoPiloto);
        return "pilotos";
    }

    @PostMapping("/pilotos/guardar")
    public String guardarPiloto(@ModelAttribute("nuevoPiloto") Piloto piloto, Model model) {
        if (piloto.getEstado() == null) {
            piloto.setEstado("Activo");
        }
        if (piloto.getDpi() == null || !piloto.getDpi().matches("[0-9]{13}")) {
            model.addAttribute("pilotos", pilotoRepository.findAll());
            model.addAttribute("error", "El DPI debe tener exactamente 13 dígitos.");
            return "pilotos";
        }
        boolean dpiDuplicado = piloto.getIdPiloto() == null
                ? pilotoRepository.existsByDpi(piloto.getDpi())
                : pilotoRepository.existsByDpiAndIdPilotoNot(piloto.getDpi(), piloto.getIdPiloto());
        boolean licenciaDuplicada = piloto.getIdPiloto() == null
                ? pilotoRepository.existsByLicencia(piloto.getLicencia())
                : pilotoRepository.existsByLicenciaAndIdPilotoNot(piloto.getLicencia(), piloto.getIdPiloto());
        if (dpiDuplicado || licenciaDuplicada) {
            model.addAttribute("pilotos", pilotoRepository.findAll());
            model.addAttribute("error", dpiDuplicado
                    ? "Ese DPI ya está registrado."
                    : "Esa licencia ya está registrada.");
            return "pilotos";
        }
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
        piloto.setDpi(pilotoDetalles.getDpi());
        piloto.setNombre(pilotoDetalles.getNombre());
        piloto.setApellido(pilotoDetalles.getApellido());
        piloto.setLicencia(pilotoDetalles.getLicencia());
        piloto.setTelefono(pilotoDetalles.getTelefono());
        if (pilotoDetalles.getEstado() != null) {
            piloto.setEstado(pilotoDetalles.getEstado());
        }
        pilotoRepository.save(piloto);
        return "redirect:/pilotos";
    }
}