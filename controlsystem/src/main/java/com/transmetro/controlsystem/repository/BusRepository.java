package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByUnidadContainingIgnoreCaseOrPlacaContainingIgnoreCase(String unidad, String placa);
    boolean existsByUnidad(String unidad);
    boolean existsByPlaca(String placa);
    boolean existsByUnidadAndIdNot(String unidad, Long id);
    boolean existsByPlacaAndIdNot(String placa, Long id);
}