package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.LineaTransmetro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineaTransmetroRepository extends JpaRepository<LineaTransmetro, Long> {
    List<LineaTransmetro> findAllByOrderByCodigoLineaAsc();
    List<LineaTransmetro> findAllByActivoTrueOrderByCodigoLineaAsc();
    boolean existsByCodigoLinea(String codigoLinea);
    Optional<LineaTransmetro> findByCodigoLinea(String codigoLinea);
}