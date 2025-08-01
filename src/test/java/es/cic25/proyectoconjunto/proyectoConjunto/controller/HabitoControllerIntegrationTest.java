package es.cic25.proyectoconjunto.proyectoConjunto.controller;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic25.proyectoconjunto.proyectoConjunto.model.Categoria;
import es.cic25.proyectoconjunto.proyectoConjunto.model.Habito;
import es.cic25.proyectoconjunto.proyectoConjunto.repository.HabitoRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class HabitoControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private HabitoRepository habitoRespository;

        @AfterEach
        void limpiarBaseDeDatos() {
                habitoRespository.deleteAll();
        }

        @Test
        void testCreate() throws Exception {

                Habito habito = new Habito();
                habito.setNombre("Meditar");
                habito.setDescripcion("Meditar cada mañana");
                habito.setEstado(true);
                habito.setCategoria(Categoria.SALUD);

                String habitoJson = objectMapper.writeValueAsString(habito);

                mockMvc.perform(post("/habito")
                                .contentType("application/json")
                                .content(habitoJson))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        String respuesta = result.getResponse().getContentAsString();
                                        Habito registroCreado = objectMapper.readValue(respuesta, Habito.class);
                                        assertTrue(registroCreado.getId() > 0, "El valor debe ser mayor que 0");

                                        Optional<Habito> registroRealmenteCreado = habitoRespository
                                                        .findById(registroCreado.getId());
                                        assertTrue(registroRealmenteCreado.isPresent());

                                });

        }

        @Test
        void testGet() throws Exception {
                // 1. Crear el hábito
                Habito habito = new Habito();
                habito.setNombre("Meditar");
                habito.setDescripcion("Meditar cada mañana");
                habito.setEstado(true);
                habito.setCategoria(Categoria.SALUD);

                // 2. Guardar el hábito en la BD
                String habitoJson = objectMapper.writeValueAsString(habito);

                // Ejecutar una solicitud post utilizando MockMvc, y guardar el resultado en un
                // objeto MvcResult
                MvcResult mvcResult = mockMvc
                                .perform(post("/habito").contentType("application/json").content(habitoJson))
                                .andReturn();

                Long id = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Habito.class).getId();

                // 3. Simular la solicitud get
                // 3.1. Realizar la solicitud HTTP GET
                mockMvc.perform(get("/habito/" + id).contentType("application/json"))
                                // Validar el estado HTTP
                                .andExpect(status().isOk())
                                // Validar el contenido del JSON
                                .andExpect(result -> {
                                        String json = result.getResponse().getContentAsString();
                                        Habito recibido = objectMapper.readValue(json, Habito.class);
                                        assertEquals("Meditar", recibido.getNombre());
                                        assertEquals("Meditar cada mañana", recibido.getDescripcion());
                                });
        }

        @Test
        void testDeleteHabito() throws Exception {
                // 1. Crear y guardar un hábito
                Habito habito = new Habito();
                habito.setNombre("Meditar");
                habito.setDescripcion("Meditar 10 minutos al día");
                habito.setEstado(true);
                habito.setCategoria(Categoria.SALUD);

                habito = habitoRespository.save(habito);

                Long id = habito.getId();

                // 2. Realizar la solicitud DELETE
                mockMvc.perform(delete("/habito/" + id))
                                .andExpect(status().isOk());

                // 3. Verificar que ya no existe en la base de datos
                Optional<Habito> eliminado = habitoRespository.findById(id);
                assertTrue(eliminado.isEmpty()); // Ya no debería estar presente
        }

        @Test
        void testUpdateHabito() throws Exception {
                Habito habito = new Habito();
                habito.setNombre("Meditar");
                habito.setDescripcion("Meditar 10 minutos al día");
                habito.setEstado(true);
                habito.setCategoria(Categoria.SALUD);

                String habitoJson = objectMapper.writeValueAsString(habito);

                // Habito habitoGuardado = habitoRespository.save(habito);
                MvcResult mvcResult = mockMvc
                                .perform(post("/habito")
                                                .contentType("application/json")
                                                .content(habitoJson))
                                .andExpect(status().isOk())
                                .andReturn();

                Habito habitoCreado = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                Habito.class);
                Long id = habitoCreado.getId();
                Long version = habitoCreado.getVersion();

                Habito habitoActualizado = new Habito();

                habitoActualizado.setId(id);
                habitoActualizado.setVersion(version);
                habitoActualizado.setNombre("Leer");
                habitoActualizado.setDescripcion("Leer un libro al mes");
                habitoActualizado.setCategoria(Categoria.CREATIVIDAD);
                habitoActualizado.setEstado(true);

                String habitoJsonModificado = objectMapper.writeValueAsString(habitoActualizado);

                mockMvc.perform(put("/habito/" + id)
                                .contentType("application/json")
                                .content(habitoJsonModificado))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                        String json = result.getResponse().getContentAsString();
                                        Habito updatedHabito = objectMapper.readValue(json, Habito.class);
                                        assertEquals("Leer un libro al mes", updatedHabito.getDescripcion());
                                        assertEquals("Leer", updatedHabito.getNombre());
                                });
        }
}
