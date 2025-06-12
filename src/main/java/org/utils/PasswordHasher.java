package org.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHasher {

    /**
     * Hashea una contraseña utilizando el algoritmo SHA-256 y la codifica en Base64.
     *
     * @param password La contraseña en texto plano que se va a hashear.
     * @return Una cadena que representa la contraseña hasheada y codificada en Base64.
     * Retorna null si el algoritmo SHA-256 no está disponible en el entorno.
     */
    public static String hashPassword(String password) {
        try {
            // Obtiene una instancia del algoritmo de resumen de mensajes SHA-256.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convierte la contraseña a bytes utilizando la codificación UTF-8 y calcula su hash.
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Codifica el array de bytes del hash resultante a una cadena utilizando la codificación Base64
            // para que sea fácilmente almacenable y transportable.
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException ex) {
            // Captura la excepción que ocurre si el algoritmo SHA-256 no está disponible.
            // En este caso, retorna null para indicar que el hasheo falló.
            return null;
        }
    }
}
