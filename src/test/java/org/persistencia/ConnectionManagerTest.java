package org.persistencia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {
    ConnectionManager connectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        // Obtiene la instancia del singleton ConnectionManager
        connectionManager = ConnectionManager.getInstance();
    }


    @AfterEach
    void tearDown() throws SQLException {
        // Se ejecuta despues de cada metodo de prueba.
        // Cierra la conexion y limpia los recursos.
        if (connectionManager != null){
            connectionManager.disconnect();
            connectionManager = null; // para asegurar que no se use accidentalmente
        }
    }

    @Test
    void connect() throws SQLException {
        // Intenta establecer una conexión a la base de datos utilizando el método connect() de ConnectionManager.
        Connection conn = connectionManager.connect();

        // Verifica que la conexión establecida no sea nula.
        assertNotNull(conn, "La conexion no debe ser nula");

        // Verifica que la conexión establecida esté abierta.
        assertFalse(conn.isClosed(), "La conexion debe estar abierta");

        if (conn != null) {
            conn.close(); // Cierra la conexión después de la prueba.
        }
    }
}
