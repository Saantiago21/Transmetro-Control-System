package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.AsignacionTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionTurnoRepository extends JpaRepository<AsignacionTurno, Long> {
}