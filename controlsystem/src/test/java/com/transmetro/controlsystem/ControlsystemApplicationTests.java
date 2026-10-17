package com.transmetro.controlsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import com.transmetro.controlsystem.model.LineaTransmetro;
import com.transmetro.controlsystem.model.ParqueoTerminal;
import com.transmetro.controlsystem.repository.LineaTransmetroRepository;
import com.transmetro.controlsystem.repository.ParqueoTerminalRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControlsystemApplicationTests {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LineaTransmetroRepository lineaRepository;

	@Autowired
	private ParqueoTerminalRepository parqueoRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void geographicTablesContainTheExpectedColumns() {
		Set<String> columns = jdbcTemplate.queryForList("""
				SELECT table_name || '.' || column_name
				FROM information_schema.columns
				WHERE table_schema = current_schema()
				  AND table_name IN ('lineas_transmetro', 'coordenadas_linea', 'estaciones')
				""", String.class).stream().collect(Collectors.toSet());

		Assertions.assertTrue(columns.containsAll(Set.of(
				"lineas_transmetro.id",
				"lineas_transmetro.codigo_linea",
				"coordenadas_linea.linea_id",
				"coordenadas_linea.orden",
				"estaciones.id",
				"estaciones.linea_id",
				"estaciones.nombre_estacion",
				"estaciones.latitud",
				"estaciones.longitud"
		)), "The geographic tables must match the mapping used by the application.");
	}

	@Test
	void homePageLoadsWithMunicipalAndParkingData() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk());
	}

	@Test
	void deactivatingLineUpdatesSameRowAndShowsInactiveOnHome() throws Exception {
		String codigo = "TEST-STATUS";
		lineaRepository.findByCodigoLinea(codigo).ifPresent(lineaRepository::delete);

		LineaTransmetro linea = new LineaTransmetro();
		linea.setCodigoLinea(codigo);
		linea.setNombre("Línea de prueba de estado");
		linea.setColorHex("#3388ff");
		linea.setActivo(true);
		linea = lineaRepository.save(linea);
		Long id = linea.getId();
		long cantidadAntes = lineaRepository.count();

		try {
			mockMvc.perform(post("/lineas/{id}/actualizar", id)
						.param("nombre", linea.getNombre())
						.param("codigoLinea", codigo)
						.param("colorHex", "#3388ff"))
					.andExpect(status().is3xxRedirection());

			Assertions.assertFalse(lineaRepository.findById(id).orElseThrow().getActivo());
			Assertions.assertEquals(cantidadAntes, lineaRepository.count());

			MvcResult home = mockMvc.perform(get("/"))
					.andExpect(status().isOk())
					.andReturn();
			Assertions.assertTrue(home.getResponse().getContentAsString().contains(codigo));
			Assertions.assertTrue(home.getResponse().getContentAsString().contains("Inactiva"));

			MvcResult mapa = mockMvc.perform(get("/api/lineas"))
					.andExpect(status().isOk())
					.andReturn();
			Assertions.assertFalse(mapa.getResponse().getContentAsString().contains(codigo));
		} finally {
			lineaRepository.deleteById(id);
		}
	}

	@Test
	void monitoringAlertsFollowParkingAvailabilityChanges() throws Exception {
		String nombre = "TEST-MONITOR-" + UUID.randomUUID();
		ParqueoTerminal parqueo = new ParqueoTerminal();
		parqueo.setNombrePatio(nombre);
		parqueo.setLibres(0);
		parqueo.setTotales(10);
		parqueo.setActivo(true);
		parqueo = parqueoRepository.save(parqueo);
		Long id = parqueo.getId();

		try {
			MvcResult agotado = mockMvc.perform(get("/api/monitoreo"))
					.andExpect(status().isOk())
					.andReturn();
			Assertions.assertTrue(agotado.getResponse().getContentAsString().contains("Capacidad agotada"));
			Assertions.assertTrue(agotado.getResponse().getContentAsString().contains(nombre));

			parqueo.setLibres(1);
			parqueoRepository.save(parqueo);
			MvcResult bajo = mockMvc.perform(get("/api/monitoreo"))
					.andExpect(status().isOk())
					.andReturn();
			Assertions.assertTrue(bajo.getResponse().getContentAsString().contains("Disponibilidad baja"));
			Assertions.assertTrue(bajo.getResponse().getContentAsString().contains(nombre));

			parqueo.setLibres(9);
			parqueoRepository.save(parqueo);
			MvcResult normal = mockMvc.perform(get("/api/monitoreo"))
					.andExpect(status().isOk())
					.andReturn();
			Assertions.assertFalse(normal.getResponse().getContentAsString().contains(nombre));
		} finally {
			parqueoRepository.deleteById(id);
		}
	}

}
