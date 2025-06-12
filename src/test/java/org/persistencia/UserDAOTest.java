package org.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.utils.PasswordHasher;
import org.dominio.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    private User create(User user) throws SQLException {
        User res = userDAO.create(user);

        assertNotNull(res, "El usuario creado no debería ser nulo.");
        assertEquals(user.getName(), res.getName(), "El nombre del usuario creado debe ser igual al original.");
        assertEquals(user.getEmail(), res.getEmail(), "El email del usuario creado debe ser igual al original.");
        assertEquals(user.getStatus(), res.getStatus(), "El status del usuario creado debe ser igual al original.");

        return res;
    }

    private void update(User user) throws SQLException {
        user.setName(user.getName() + "_u");
        user.setEmail("u" + user.getEmail());
        user.setStatus((byte) 1);

        boolean res = userDAO.update(user);
        assertTrue(res, "La actualización del usuario debería ser exitosa.");

        getById(user);
    }

    private void getById(User user) throws SQLException {
        User res = userDAO.getById(user.getId());

        assertNotNull(res, "El usuario obtenido por ID no debería ser nulo.");
        assertEquals(user.getId(), res.getId(), "El ID del usuario obtenido debe ser igual al original.");
        assertEquals(user.getName(), res.getName(), "El nombre del usuario obtenido debe ser igual al esperado.");
        assertEquals(user.getEmail(), res.getEmail(), "El email del usuario obtenido debe ser igual al esperado.");
        assertEquals(user.getStatus(), res.getStatus(), "El status del usuario obtenido debe ser igual al esperado.");
    }

    private void search(User user) throws SQLException {
        ArrayList<User> users = userDAO.search(user.getName());
        boolean find = true;

        for (User userItem : users) {
            if (!userItem.getName().contains(user.getName())) {
                find = false;
                break;
            }
        }

        assertTrue(find, "El nombre buscado no fue encontrado: " + user.getName());
    }

    private void delete(User user) throws SQLException {
        boolean res = userDAO.delete(user);
        assertTrue(res, "La eliminación del usuario debería ser exitosa.");

        User res2 = userDAO.getById(user.getId());
        assertNull(res2, "El usuario debería haber sido eliminado y no encontrado por ID.");
    }

    private void authenticate(User user) throws SQLException {
        User res = userDAO.authenticate(user);

        assertNotNull(res, "La autenticación debería retornar un usuario no nulo si es exitosa.");
        assertEquals(user.getEmail(), res.getEmail(), "El email del usuario autenticado debe coincidir con el email proporcionado.");
        assertEquals((byte)1, res.getStatus(), "El status del usuario autenticado debe ser 1 (activo).");
    }

    private void authenticationFails(User user) throws SQLException {
        User res = userDAO.authenticate(user);
        assertNull(res, "La autenticación debería fallar y retornar null para credenciales inválidas.");
    }

    private void updatePassword(User user, String newPassword) throws SQLException {
        boolean res = userDAO.updatePassword(user.getId(), newPassword);
        assertTrue(res, "La actualización de la contraseña debería ser exitosa.");

        user.setPasswordHash(PasswordHasher.hashPassword(newPassword));
        authenticate(user);
    }

    @Test
    void testUserDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String email = "test" + num + "@example.com";
        String rawPassword = "password";

        User user = new User(0, "Test User", PasswordHasher.hashPassword(rawPassword), email, (byte) 2);

        User testUser = create(user);

        update(testUser);
        search(testUser);

        testUser.setPasswordHash(PasswordHasher.hashPassword(rawPassword));
        authenticate(testUser);

        testUser.setPasswordHash(PasswordHasher.hashPassword("12345"));
        authenticationFails(testUser);

        updatePassword(testUser, "new_password");

        delete(testUser);
    }

    @Test
    void createUser() throws SQLException {
        String rawPassword = "12345";
        User user = new User(0, "admin", PasswordHasher.hashPassword(rawPassword), "admin@gmail.com", (byte) 1);
        User res = userDAO.create(user);
        assertNotNull(res, "El usuario creado no debe ser nulo.");
    }
}
