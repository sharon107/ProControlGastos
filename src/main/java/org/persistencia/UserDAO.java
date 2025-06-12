package org.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.dominio.User;
import org.utils.PasswordHasher;

public class UserDAO {
    private ConnectionManager conn;

    public UserDAO() {
        conn = ConnectionManager.getInstance();
    }

    public User create(User user) throws SQLException {
        User res = null;
        String sql = "INSERT INTO Users(name, passwordHash, email, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            // Aquí NO vuelvas a hashear, el user ya trae el hash
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getEmail());
            ps.setByte(4, user.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    }
                }
            }
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public boolean update(User user) throws SQLException {
        boolean res = false;
        String sql = "UPDATE Users SET name = ?, email = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setByte(3, user.getStatus());
            ps.setInt(4, user.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public boolean delete(User user) throws SQLException {
        boolean res = false;
        String sql = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } finally {
            conn.disconnect();
        }
        return res;
    }

    public User getById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("passwordHash"),
                            rs.getString("email"),
                            rs.getByte("status")
                    );
                }
            }
        } finally {
            conn.disconnect();
        }
        return user;
    }

    public User authenticate(User user) throws SQLException {
        User userAuthenticate = null;
        String sql = "SELECT id, name, email, status FROM Users WHERE email = ? AND passwordHash = ? AND status = 1";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            // Aquí NO vuelvas a hashear, ya debe venir el hash en user
            ps.setString(2, user.getPasswordHash());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userAuthenticate = new User();
                    userAuthenticate.setId(rs.getInt(1));
                    userAuthenticate.setName(rs.getString(2));
                    userAuthenticate.setEmail(rs.getString(3));
                    userAuthenticate.setStatus(rs.getByte(4));
                }
            }
        } finally {
            conn.disconnect();
        }
        return userAuthenticate;
    }

    public ArrayList<User> search(String name) throws SQLException {
        ArrayList<User> result = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE name LIKE ?";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("passwordHash"),
                            rs.getString("email"),
                            rs.getByte("status")
                    );
                    result.add(user);
                }
            }
        } finally {
            conn.disconnect();
        }
        return result;
    }

    public boolean updatePassword(int userId, String newPlainPassword) throws SQLException {
        boolean res = false;
        String sql = "UPDATE Users SET passwordHash = ? WHERE id = ?";
        try (PreparedStatement ps = conn.connect().prepareStatement(sql)) {
            ps.setString(1, PasswordHasher.hashPassword(newPlainPassword));  // Aquí SÍ haces el hash porque recibes la contraseña en texto plano
            ps.setInt(2, userId);
            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } finally {
            conn.disconnect();
        }
        return res;
    }
}
