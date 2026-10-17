package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstacionRepository extends JpaRepository<Estacion, Long> {
    List<Estacion> findAllByLinea_IdOrderByIdAsc(Long lineaId);
}