package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.ParqueoTerminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParqueoTerminalRepository extends JpaRepository<ParqueoTerminal, Long> {
}