package es.cic25.proyectoconjunto.proyectoConjunto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.cic25.proyectoconjunto.proyectoConjunto.model.Habito;
import es.cic25.proyectoconjunto.proyectoConjunto.repository.HabitoRepository;

@SpringBootTest
public class HabitoServiceIntegrationTest {

    @Autowired
    private HabitoService habitoService;

    @Autowired
    private HabitoRepository habitoRespository;

    @AfterEach
    void limpiarBaseDeDatos() {
        habitoRespository.deleteAll();
    }

    @Test
    void testCreate() {

        String nombreHabito = "habito";

        Habito habito = new Habito();
        habito.setNombre("habito");

        Habito habito2 = habitoService.create(habito);

        assertEquals(nombreHabito, habito2.getNombre());
    }

    @Test
    void testGet() {
        Habito habito = new Habito();
        habito.setNombre("leer");
        Habito guardado = habitoService.create(habito);

        Optional<Habito> obtenido = habitoService.get(guardado.getId());

        assertTrue(obtenido.isPresent());
        assertEquals("leer", obtenido.get().getNombre());
    }

    @Test
    void testGetAll() {
        Habito h1 = new Habito();
        h1.setNombre("caminar");
        Habito h2 = new Habito();
        h2.setNombre("leer");

        habitoService.create(h1);
        habitoService.create(h2);

        List<Habito> lista = habitoService.getAll();

        assertEquals(2, lista.size());
    }

    @Test
    void testDelete() {
        Habito habito = new Habito();
        habito.setNombre("meditar");
        Habito guardado = habitoService.create(habito);

        Long idAComprobar = guardado.getId();

        habitoService.delete(idAComprobar);

        Optional<Habito> eliminado = habitoService.get(idAComprobar);

        assertFalse(eliminado.isPresent());
    }
}
