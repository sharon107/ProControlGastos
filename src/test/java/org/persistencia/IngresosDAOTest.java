package org.persistencia;

import org.dominio.Ingresos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class IngresosDAOTest {

    private IngresosDAO ingresosDAO;

    @BeforeEach
    void setUp() {
        ingresosDAO = new IngresosDAO(); // Inicializa el DAO antes de cada prueba
    }

    private Ingresos create(Ingresos ingreso) throws SQLException {
        Ingresos res = ingresosDAO.create(ingreso);

        assertNotNull(res, "El ingreso creado no debería ser nulo.");
        assertTrue(res.getId() > 0, "El ID del ingreso debe ser mayor que 0.");
        assertEquals(ingreso.getUserId(), res.getUserId(), "El userId debe coincidir.");
        assertEquals(ingreso.getFuente(), res.getFuente(), "La fuente debe coincidir.");
        assertEquals(ingreso.getMonto(), res.getMonto(), "El monto debe coincidir.");
        assertEquals(ingreso.getFecha(), res.getFecha(), "La fecha debe coincidir.");
        assertEquals(ingreso.getDescripcion(), res.getDescripcion(), "La descripción debe coincidir.");

        return res;
    }

    private void update(Ingresos ingreso) throws SQLException {
        ingreso.setDescripcion(ingreso.getDescripcion() + " actualizado");
        ingreso.setMonto(ingreso.getMonto().add(new BigDecimal("15.00")));

        boolean actualizado = ingresosDAO.update(ingreso);
        assertTrue(actualizado, "El ingreso debería actualizarse correctamente.");

        getById(ingreso);
    }

    private void getById(Ingresos ingreso) throws SQLException {
        Ingresos res = ingresosDAO.getById(ingreso.getId());

        assertNotNull(res, "El ingreso obtenido no debería ser nulo.");
        assertEquals(ingreso.getId(), res.getId(), "Los ID deben coincidir.");
        assertEquals(ingreso.getDescripcion(), res.getDescripcion(), "Las descripciones deben coincidir.");
        assertEquals(ingreso.getMonto(), res.getMonto(), "Los montos deben coincidir.");
    }

    private void getByUserId(Ingresos ingreso) throws SQLException {
        ArrayList<Ingresos> lista = ingresosDAO.getByUserId(ingreso.getUserId());

        boolean encontrado = false;
        for (Ingresos i : lista) {
            if (i.getId() == ingreso.getId()) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "El ingreso debería estar en la lista de resultados por userId.");
    }

    private void delete(Ingresos ingreso) throws SQLException {
        boolean eliminado = ingresosDAO.delete(ingreso.getId());
        assertTrue(eliminado, "El ingreso debería eliminarse correctamente.");

        Ingresos eliminadoCheck = ingresosDAO.getById(ingreso.getId());
        assertNull(eliminadoCheck, "El ingreso eliminado no debería existir en la base de datos.");
    }

    @Test
    void testCRUDIngresos() throws SQLException {
        Random random = new Random();
        int userId = 1; // Asegúrate que este userId exista en la base de datos
        BigDecimal monto = new BigDecimal(random.nextInt(500) + ".00");
        String fuente = "Trabajo freelance";
        Date fecha = Date.valueOf("2025-06-18");
        String descripcion = "Ingreso prueba " + random.nextInt(1000);

        Ingresos ingreso = new Ingresos(0, userId, monto, fuente, fecha, descripcion);

        Ingresos testIngreso = create(ingreso);
        update(testIngreso);
        getByUserId(testIngreso);
        delete(testIngreso);
    }

    @Test
    void crearIngresoBasico() throws SQLException {
        Ingresos ingreso = new Ingresos(0, 1, new BigDecimal("45.00"), "Regalo", Date.valueOf("2025-06-18"), "Cumpleaños");
        Ingresos res = ingresosDAO.create(ingreso);
        assertNotNull(res, "El ingreso creado no debería ser nulo.");
    }
}
