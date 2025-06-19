package org.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.dominio.Gasto;

public class GastoDAO {

    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public GastoDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo registro de gasto en la base de datos.
     * El ID del gasto es generado automáticamente por la base de datos.
     *
     * @param gasto El objeto Gasto con la información a guardar.
     * @return El objeto Gasto recién creado, incluyendo el ID generado.
     * @throws SQLException si ocurre un error al interactuar con la base de datos.
     */
    public Gasto create(Gasto gasto) throws SQLException {
        Gasto gastoCreado = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO Gastos (UserId, Monto, Categoria, Fecha, Descripcion) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            ); // [10]
            ps.setInt(1, gasto.getUserId()); // [10]
            ps.setBigDecimal(2, gasto.getMonto()); // [10]
            ps.setString(3, gasto.getCategoria()); // [10]
            ps.setDate(4, gasto.getFecha()); // [10]
            ps.setString(5, gasto.getDescripcion()); // [10]

            if (ps.executeUpdate() > 0) { // [10]
                rs = ps.getGeneratedKeys(); // [10]
                if (rs.next()) { // [10]
                    int idGenerado = rs.getInt(1); // [11]
                    gastoCreado = getById(idGenerado); // [11]
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el gasto: " + ex.getMessage(), ex); // [11]
        } finally {
            if (rs != null) rs.close(); // [11]
            if (ps != null) ps.close(); // [11]
            conn.disconnect(); // [11]
        }
        return gastoCreado;
    }

    /**
     * Actualiza un gasto existente en la base de datos.
     *
     * @param gasto El objeto Gasto con los datos a actualizar. Se identifica por su ID.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws SQLException si ocurre un error al interactuar con la base de datos.
     */
    public boolean update(Gasto gasto) throws SQLException {
        boolean resultado = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Gastos SET UserId = ?, Monto = ?, Categoria = ?, Fecha = ?, Descripcion = ? WHERE Id = ?"
            ); // [11]
            ps.setInt(1, gasto.getUserId()); // [11]
            ps.setBigDecimal(2, gasto.getMonto()); // [11]
            ps.setString(3, gasto.getCategoria()); // [11]
            ps.setDate(4, gasto.getFecha()); // [11]
            ps.setString(5, gasto.getDescripcion()); // [11]
            ps.setInt(6, gasto.getId()); // [11]

            if (ps.executeUpdate() > 0) { // [11]
                resultado = true; // [11]
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al modificar el gasto: " + ex.getMessage(), ex); // [11]
        } finally {
            if (ps != null) ps.close(); // [11]
            conn.disconnect(); // [11]
        }
        return resultado; // [12]
    }

    /**
     * Elimina un gasto de la base de datos por su ID.
     *
     * @param gastoId El ID del gasto a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException si ocurre un error al interactuar con la base de datos.
     */
    public boolean delete(int gastoId) throws SQLException {
        boolean resultado = false;
        try {
            ps = conn.connect().prepareStatement("DELETE FROM Gastos WHERE Id = ?"); // [12]
            ps.setInt(1, gastoId); // [12]

            if (ps.executeUpdate() > 0) { // [12]
                resultado = true; // [12]
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el gasto: " + ex.getMessage(), ex); // [12]
        } finally {
            if (ps != null) ps.close(); // [12]
            conn.disconnect(); // [12]
        }
        return resultado; // [12]
    }

    /**
     * Obtiene un gasto específico de la base de datos por su ID.
     *
     * @param gastoId El ID del gasto a buscar.
     * @return Un objeto Gasto si se encuentra, o null si no existe.
     * @throws SQLException si ocurre un error al interactuar con la base de datos.
     */
    public Gasto getById(int gastoId) throws SQLException {
        Gasto gasto = null;
        try {
            ps = conn.connect().prepareStatement("SELECT Id, UserId, Monto, Categoria, Fecha, Descripcion FROM Gastos WHERE Id = ?"); // [12]
            ps.setInt(1, gastoId); // [12]
            rs = ps.executeQuery(); // [12]

            if (rs.next()) { // [12]
                gasto = new Gasto(); // [13]
                gasto.setId(rs.getInt("Id")); // [13]
                gasto.setUserId(rs.getInt("UserId")); // [13]
                gasto.setMonto(rs.getBigDecimal("Monto")); // [13]
                gasto.setCategoria(rs.getString("Categoria")); // [13]
                gasto.setFecha(rs.getDate("Fecha")); // [13]
                gasto.setDescripcion(rs.getString("Descripcion")); // [13]
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener gasto por ID: " + ex.getMessage(), ex); // [13]
        } finally {
            if (rs != null) rs.close(); // [13]
            if (ps != null) ps.close(); // [13]
            conn.disconnect(); // [13]
        }
        return gasto; // [13]
    }

    /**
     * Obtiene todos los gastos registrados para un usuario específico.
     *
     * @param userId El ID del usuario cuyos gastos se desean obtener.
     * @return Una lista (ArrayList) de objetos Gasto. La lista estará vacía si no hay gastos.
     * @throws SQLException si ocurre un error al interactuar con la base de datos.
     */
    public ArrayList<Gasto> getByUserId(int userId) throws SQLException {
        ArrayList<Gasto> gastos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement("SELECT Id, UserId, Monto, Categoria, Fecha, Descripcion FROM Gastos WHERE UserId = ? ORDER BY Fecha DESC"); // [13]
            ps.setInt(1, userId); // [13]
            rs = ps.executeQuery(); // [13]

            while (rs.next()) { // [13]
                Gasto gasto = new Gasto(); // [13]
                gasto.setId(rs.getInt("Id")); // [13]
                gasto.setUserId(rs.getInt("UserId")); // [13]
                gasto.setMonto(rs.getBigDecimal("Monto")); // [13]
                gasto.setCategoria(rs.getString("Categoria")); // [13]
                gasto.setFecha(rs.getDate("Fecha")); // [13]
                gasto.setDescripcion(rs.getString("Descripcion")); // [13]
                gastos.add(gasto); // [13]
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener gastos por usuario: " + ex.getMessage(), ex); // [14]
        } finally {
            if (rs != null) rs.close(); // [14]
            if (ps != null) ps.close(); // [14]
            conn.disconnect(); // [14]
        }
        return gastos; // [14]
    }

    // --- NUEVOS MÉTODOS AÑADIDOS PARA SOPORTAR GastoReadingForm ---

    /**
     * Obtiene todos los gastos de la base de datos.
     * Es necesario para el método refreshTable() de GastoReadingForm.
     *
     * @return Un ArrayList con todos los objetos Gasto.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public ArrayList<Gasto> getAll() throws SQLException {
        ArrayList<Gasto> records = new ArrayList<>();
        try {
            // Consulta SQL para seleccionar todos los campos de todos los gastos
            ps = conn.connect().prepareStatement("SELECT Id, UserId, Monto, Categoria, Fecha, Descripcion FROM Gastos");
            rs = ps.executeQuery(); // Ejecuta la consulta

            // Itera sobre los resultados y crea objetos Gasto, similar a getByUserId [13]
            while (rs.next()) {
                Gasto gasto = new Gasto();
                gasto.setId(rs.getInt("Id"));
                gasto.setUserId(rs.getInt("UserId"));
                gasto.setMonto(rs.getBigDecimal("Monto"));
                gasto.setCategoria(rs.getString("Categoria"));
                gasto.setFecha(rs.getDate("Fecha"));
                gasto.setDescripcion(rs.getString("Descripcion"));
                records.add(gasto); // Añade el gasto a la lista
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener todos los gastos: " + ex.getMessage(), ex);
        } finally {
            // Asegura que los recursos de la base de datos se cierren, similar a otros métodos [11-14]
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return records;
    }

    /**
     * Busca gastos cuya categoría o descripción contengan la cadena proporcionada.
     * Es necesario para el método search() de GastoReadingForm.
     *
     * @param query La cadena de texto a buscar.
     * @return Una lista de gastos que coinciden.
     * @throws SQLException si ocurre un error de base de datos.
     */
    public ArrayList<Gasto> search(String query) throws SQLException {
        ArrayList<Gasto> records = new ArrayList<>();
        try {
            // Consulta SQL para buscar gastos por Categoria o Descripcion, similar a UserDAO.search [5]
            ps = conn.connect().prepareStatement("SELECT Id, UserId, Monto, Categoria, Fecha, Descripcion FROM Gastos WHERE Categoria LIKE ? OR Descripcion LIKE ?");
            ps.setString(1, "%" + query + "%"); // Permite buscar coincidencias parciales [5]
            ps.setString(2, "%" + query + "%");
            rs = ps.executeQuery(); // Ejecuta la consulta

            // Itera sobre los resultados y crea objetos Gasto, similar a getAll y getByUserId [13]
            while (rs.next()) {
                Gasto gasto = new Gasto();
                gasto.setId(rs.getInt("Id"));
                gasto.setUserId(rs.getInt("UserId"));
                gasto.setMonto(rs.getBigDecimal("Monto"));
                gasto.setCategoria(rs.getString("Categoria"));
                gasto.setFecha(rs.getDate("Fecha"));
                gasto.setDescripcion(rs.getString("Descripcion"));
                records.add(gasto); // Añade el gasto a la lista
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar gastos: " + ex.getMessage(), ex);
        } finally {
            // Asegura que los recursos de la base de datos se cierren [11-14]
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return records;
    }
}