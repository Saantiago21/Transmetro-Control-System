package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.Rol;
import com.transmetro.controlsystem.model.Usuario;
import com.transmetro.controlsystem.repository.RolRepository;
import com.transmetro.controlsystem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("nuevoUsuario", new Usuario());
        return "usuarios";
    }

    @PostMapping("/usuarios/guardar")
    public RedirectView guardarUsuario(@ModelAttribute("nuevoUsuario") Usuario usuario, 
                                       @RequestParam("rolId") Long rolId) {
        // Convertimos el Long del formulario a Integer para que coincida con el repositorio de Roles
        Rol rol = rolRepository.findById(rolId.intValue()).orElse(null);
        if (rol != null) {
            usuario.setRol(rol);
        }
        
        // Asignar estado activo (Boolean true)
        usuario.setEstado(true);

        usuarioRepository.save(usuario);
        return new RedirectView("/usuarios", true);
    }
}