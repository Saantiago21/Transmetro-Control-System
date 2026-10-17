package com.transmetro.controlsystem.repository;

import com.transmetro.controlsystem.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByCodigoRutaContainingIgnoreCaseOrNombreRutaContainingIgnoreCase(String codigoRuta, String nombreRuta);
    boolean existsByCodigoRuta(String codigoRuta);
}