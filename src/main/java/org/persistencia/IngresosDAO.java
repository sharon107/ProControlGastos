package org.persistencia;

import org.dominio.Ingresos;

import java.sql.*;
import java.util.ArrayList;

public class IngresosDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public IngresosDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Ingresos create(Ingresos ingreso) throws SQLException {
        Ingresos creado = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO Ingresos (UserId, Monto, Fuente, Fecha, Descripcion) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, ingreso.getUserId());
            ps.setBigDecimal(2, ingreso.getMonto());
            ps.setString(3, ingreso.getFuente());
            ps.setDate(4, ingreso.getFecha());
            ps.setString(5, ingreso.getDescripcion());

            if (ps.executeUpdate() > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    creado = getById(rs.getInt(1));
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return creado;
    }

    public boolean update(Ingresos ingreso) throws SQLException {
        boolean actualizado = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Ingresos SET UserId=?, Monto=?, Fuente=?, Fecha=?, Descripcion=? WHERE Id=?"
            );
            ps.setInt(1, ingreso.getUserId());
            ps.setBigDecimal(2, ingreso.getMonto());
            ps.setString(3, ingreso.getFuente());
            ps.setDate(4, ingreso.getFecha());
            ps.setString(5, ingreso.getDescripcion());
            ps.setInt(6, ingreso.getId());

            actualizado = ps.executeUpdate() > 0;
        } finally {
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return actualizado;
    }

    public boolean delete(int id) throws SQLException {
        boolean eliminado = false;
        try {
            ps = conn.connect().prepareStatement("DELETE FROM Ingresos WHERE Id=?");
            ps.setInt(1, id);
            eliminado = ps.executeUpdate() > 0;
        } finally {
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return eliminado;
    }

    public Ingresos getById(int id) throws SQLException {
        Ingresos ingreso = null;
        try {
            ps = conn.connect().prepareStatement("SELECT * FROM Ingresos WHERE Id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                ingreso = new Ingresos(
                        rs.getInt("Id"),
                        rs.getInt("UserId"),
                        rs.getBigDecimal("Monto"),
                        rs.getString("Fuente"),
                        rs.getDate("Fecha"),
                        rs.getString("Descripcion")
                );
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return ingreso;
    }

    public ArrayList<Ingresos> getAll() throws SQLException {
        ArrayList<Ingresos> lista = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement("SELECT * FROM Ingresos");
            rs = ps.executeQuery();

            while (rs.next()) {
                Ingresos ingreso = new Ingresos(
                        rs.getInt("Id"),
                        rs.getInt("UserId"),
                        rs.getBigDecimal("Monto"),
                        rs.getString("Fuente"),
                        rs.getDate("Fecha"),
                        rs.getString("Descripcion")
                );
                lista.add(ingreso);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return lista;
    }

    public ArrayList<Ingresos> search(String texto) throws SQLException {
        ArrayList<Ingresos> lista = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement("SELECT * FROM Ingresos WHERE Fuente LIKE ? OR Descripcion LIKE ?");
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Ingresos ingreso = new Ingresos(
                        rs.getInt("Id"),
                        rs.getInt("UserId"),
                        rs.getBigDecimal("Monto"),
                        rs.getString("Fuente"),
                        rs.getDate("Fecha"),
                        rs.getString("Descripcion")
                );
                lista.add(ingreso);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return lista;
    }

    public ArrayList<Ingresos> getByUserId(int userId) throws SQLException {
        ArrayList<Ingresos> lista = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement("SELECT * FROM Ingresos WHERE UserId = ?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Ingresos ingreso = new Ingresos(
                        rs.getInt("Id"),
                        rs.getInt("UserId"),
                        rs.getBigDecimal("Monto"),
                        rs.getString("Fuente"),
                        rs.getDate("Fecha"),
                        rs.getString("Descripcion")
                );
                lista.add(ingreso);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return lista;
    }
}
