package org.persistencia;

import org.dominio.Gasto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GastoDAOTest {

    private GastoDAO gastoDAO;

    @BeforeEach
    void setUp() {
        gastoDAO = new GastoDAO(); // Inicializa el DAO antes de cada prueba
    }

    private Gasto create(Gasto gasto) throws SQLException {
        Gasto res = gastoDAO.create(gasto);

        assertNotNull(res, "El gasto creado no debería ser nulo.");
        assertTrue(res.getId() > 0, "El ID del gasto debe ser mayor que 0.");
        assertEquals(gasto.getUserId(), res.getUserId(), "El userId debe coincidir.");
        assertEquals(gasto.getCategoria(), res.getCategoria(), "La categoría debe coincidir.");
        assertEquals(gasto.getMonto(), res.getMonto(), "El monto debe coincidir.");
        assertEquals(gasto.getFecha(), res.getFecha(), "La fecha debe coincidir.");
        assertEquals(gasto.getDescripcion(), res.getDescripcion(), "La descripción debe coincidir.");

        return res;
    }

    private void update(Gasto gasto) throws SQLException {
        gasto.setDescripcion(gasto.getDescripcion() + " actualizado");
        gasto.setMonto(gasto.getMonto().add(new BigDecimal("10.00")));

        boolean actualizado = gastoDAO.update(gasto);
        assertTrue(actualizado, "El gasto debería actualizarse correctamente.");

        getById(gasto);
    }

    private void getById(Gasto gasto) throws SQLException {
        Gasto res = gastoDAO.getById(gasto.getId());

        assertNotNull(res, "El gasto obtenido no debería ser nulo.");
        assertEquals(gasto.getId(), res.getId(), "Los ID deben coincidir.");
        assertEquals(gasto.getDescripcion(), res.getDescripcion(), "Las descripciones deben coincidir.");
        assertEquals(gasto.getMonto(), res.getMonto(), "Los montos deben coincidir.");
    }

    private void searchByUserId(Gasto gasto) throws SQLException {
        ArrayList<Gasto> lista = gastoDAO.getByUserId(gasto.getUserId());

        boolean encontrado = false;
        for (Gasto g : lista) {
            if (g.getId() == gasto.getId()) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "El gasto debería estar en la lista de resultados por userId.");
    }

    private void delete(Gasto gasto) throws SQLException {
        boolean eliminado = gastoDAO.delete(gasto.getId());
        assertTrue(eliminado, "El gasto debería eliminarse correctamente.");

        Gasto eliminadoCheck = gastoDAO.getById(gasto.getId());
        assertNull(eliminadoCheck, "El gasto eliminado no debería existir en la base de datos.");
    }

    @Test
    void testCRUDGasto() throws SQLException {
        Random random = new Random();
        int userId = 1; // Asegúrate que este userId exista en tu tabla de usuarios
        BigDecimal monto = new BigDecimal(random.nextInt(100) + ".50");
        String categoria = "Prueba";
        Date fecha = Date.valueOf("2025-06-18");
        String descripcion = "Test Gasto " + random.nextInt(1000);

        Gasto gasto = new Gasto(0, userId, monto, categoria, fecha, descripcion);

        Gasto testGasto = create(gasto);
        update(testGasto);
        searchByUserId(testGasto);
        delete(testGasto);
    }

    @Test
    void crearGastoBasico() throws SQLException {
        Gasto gasto = new Gasto(0, 1, new BigDecimal("20.00"), "Transporte", Date.valueOf("2025-06-18"), "Bus");
        Gasto res = gastoDAO.create(gasto);
        assertNotNull(res, "El gasto creado no debería ser nulo.");
    }
}
