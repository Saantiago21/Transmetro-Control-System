package com.transmetro.controlsystem.controller;

import com.transmetro.controlsystem.model.Usuario;
import com.transmetro.controlsystem.repository.RolRepository;
import com.transmetro.controlsystem.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // Muestra la vista de Login / Registro
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("roles", rolRepository.findAll());
        return "login";
    }

    // Procesa el inicio de sesión de usuarios existentes
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo, 
                                @RequestParam String passwordHash, 
                                @RequestParam Integer idRol,
                                HttpSession session, 
                                Model model) {
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "El correo electrónico no está registrado. Puedes crear tu cuenta en la pestaña 'Registrarse'.");
            model.addAttribute("roles", rolRepository.findAll());
            return "login";
        }

        Usuario usuario = usuarioOpt.get();

        // Validación especial para Administrador (contraseña fija requerida: admin123)
        boolean esAdmin = usuario.getRol() != null && 
                          usuario.getRol().getNombreRol().toLowerCase().contains("admin");

        if (esAdmin) {
            if (!passwordHash.equals("admin123")) {
                model.addAttribute("error", "Contraseña de Administrador incorrecta (debe ser admin123)");
                model.addAttribute("roles", rolRepository.findAll());
                return "login";
            }
        } else {
            if (!usuario.getPasswordHash().equals(passwordHash)) {
                model.addAttribute("error", "Contraseña incorrecta");
                model.addAttribute("roles", rolRepository.findAll());
                return "login";
            }
        }

        if (usuario.getEstado() != null && !usuario.getEstado()) {
            model.addAttribute("error", "Su usuario está inactivo. Contacte a un administrador.");
            model.addAttribute("roles", rolRepository.findAll());
            return "login";
        }

        // Guardar datos en sesión
        session.setAttribute("usuarioLogueado", usuario);
        session.setAttribute("rolUsuario", usuario.getRol().getNombreRol());

        return "redirect:/";
    }

    // Procesa el registro completo de un nuevo usuario
    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String correo,
                                   @RequestParam String passwordHash,
                                   @RequestParam Integer idRol,
                                   HttpSession session,
                                   Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            model.addAttribute("roles", rolRepository.findAll());
            return "login";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setPasswordHash(passwordHash);
        nuevoUsuario.setRol(rolRepository.findById(idRol).orElse(null));
        nuevoUsuario.setEstado(true);
        nuevoUsuario.setFechaCreacion(LocalDateTime.now());

        usuarioRepository.save(nuevoUsuario);

        // Iniciar sesión automáticamente tras el registro
        session.setAttribute("usuarioLogueado", nuevoUsuario);
        session.setAttribute("rolUsuario", nuevoUsuario.getRol().getNombreRol());

        return "redirect:/";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}