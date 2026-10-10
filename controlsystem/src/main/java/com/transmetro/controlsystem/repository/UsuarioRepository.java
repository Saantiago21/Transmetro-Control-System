package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Método auxiliar útil para futuros logins o validaciones por correo
    Optional<Usuario> findByCorreo(String correo);
}