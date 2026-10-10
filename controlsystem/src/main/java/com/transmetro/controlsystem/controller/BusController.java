package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.Bus;
import com.transmetro.controlsystem.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BusController {

    @Autowired
    private BusRepository busRepository;

    @GetMapping("/buses")
    public String listarBuses(Model model, @RequestParam(name = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("buses", busRepository.findByUnidadContainingIgnoreCaseOrPlacaContainingIgnoreCase(keyword, keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("buses", busRepository.findAll());
        }
        model.addAttribute("nuevoBus", new Bus());
        return "buses";
    }

    @PostMapping("/buses/guardar")
    public String guardarBus(@ModelAttribute("nuevoBus") Bus bus) {
        busRepository.save(bus);
        return "redirect:/buses";
    }

    @GetMapping("/buses/editar/{id}")
    public String mostrarFormularioEditarBus(@PathVariable("id") Long id, Model model) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de bus inválido:" + id));
        model.addAttribute("nuevoBus", bus);
        model.addAttribute("buses", busRepository.findAll());
        return "buses";
    }

    @PostMapping("/buses/actualizar/{id}")
    public String actualizarBus(@PathVariable("id") Long id, @ModelAttribute("nuevoBus") Bus busDetalles) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de bus inválido:" + id));
        bus.setUnidad(busDetalles.getUnidad());
        bus.setPlaca(busDetalles.getPlaca());
        bus.setCapacidad(busDetalles.getCapacidad());
        bus.setEstado(busDetalles.getEstado());
        busRepository.save(bus);
        return "redirect:/buses";
    }
}