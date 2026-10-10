package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.Ruta;
import com.transmetro.controlsystem.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RutaController {

    @Autowired
    private RutaRepository rutaRepository;

    @GetMapping("/rutas")
    public String listarRutas(Model model, @RequestParam(name = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("rutas", rutaRepository.findByCodigoRutaContainingIgnoreCaseOrNombreRutaContainingIgnoreCase(keyword, keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("rutas", rutaRepository.findAll());
        }
        model.addAttribute("nuevaRuta", new Ruta());
        return "rutas";
    }

    @PostMapping("/rutas/guardar")
    public String guardarRuta(@ModelAttribute("nuevaRuta") Ruta ruta) {
        rutaRepository.save(ruta);
        return "redirect:/rutas";
    }

    @GetMapping("/rutas/editar/{id}")
    public String mostrarFormularioEditarRuta(@PathVariable("id") Long id, Model model) {
        Ruta ruta = rutaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de ruta inválido:" + id));
        model.addAttribute("nuevaRuta", ruta);
        model.addAttribute("rutas", rutaRepository.findAll());
        return "rutas";
    }

    @PostMapping("/rutas/actualizar/{id}")
    public String actualizarRuta(@PathVariable("id") Long id, @ModelAttribute("nuevaRuta") Ruta rutaDetalles) {
        Ruta ruta = rutaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de ruta inválido:" + id));
        ruta.setCodigoRuta(rutaDetalles.getCodigoRuta());
        ruta.setNombreRuta(rutaDetalles.getNombreRuta());
        ruta.setDescripcion(rutaDetalles.getDescripcion());
        ruta.setEstado(rutaDetalles.getEstado());
        rutaRepository.save(ruta);
        return "redirect:/rutas";
    }
}