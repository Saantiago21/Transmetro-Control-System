package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.Usuario;
import com.transmetro.controlsystem.repository.RolRepository;
import com.transmetro.controlsystem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class HomeController {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) Integer editId) {
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        
        if (editId != null) {
            Usuario u = usuarioRepository.findById(editId).orElse(new Usuario());
            model.addAttribute("nuevoUsuario", u);
            model.addAttribute("modoEdicion", true);
        } else {
            model.addAttribute("nuevoUsuario", new Usuario());
            model.addAttribute("modoEdicion", false);
        }
        return "index";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("nuevoUsuario") Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            usuario.setEstado(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isEmpty()) {
                usuario.setPasswordHash("123456");
            }
        } else {
            Usuario exist = usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
            if (exist != null) {
                usuario.setFechaCreacion(exist.getFechaCreacion());
                usuario.setEstado(exist.getEstado());
                usuario.setPasswordHash(exist.getPasswordHash());
            }
        }
        usuarioRepository.save(usuario);
        return "redirect:/";
    }

    // Mapeo añadido para resolver el error 404 del botón de editar
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Integer id) {
        return "redirect:/?editId=" + id;
    }

    @GetMapping("/usuarios/estado/{id}")
    public String cambiarEstado(@PathVariable Integer id) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            u.setEstado(!u.getEstado());
            usuarioRepository.save(u);
        }
        return "redirect:/";
    }
}