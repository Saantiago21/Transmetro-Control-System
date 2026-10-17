package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.ReporteOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteOperativoRepository extends JpaRepository<ReporteOperativo, Long> {
}