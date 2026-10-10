package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PilotoRepository extends JpaRepository<Piloto, Long> {
    List<Piloto> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrLicenciaContainingIgnoreCase(String nombre, String apellido, String licencia);
}