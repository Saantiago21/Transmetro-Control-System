package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.MunicipalidadZona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalidadZonaRepository extends JpaRepository<MunicipalidadZona, Long> {
}