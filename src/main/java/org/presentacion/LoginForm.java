package org.presentacion;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.dominio.User;
import org.persistencia.UserDAO;

public class LoginForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSalir;

    private UserDAO userDAO;
    private MainForm mainForm;

    public LoginForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UserDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Login");
        pack();
        setLocationRelativeTo(mainForm);

        btnSalir.addActionListener(e -> System.exit(0));
        btnLogin.addActionListener(e -> login());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void login() {
        try {
            User user = new User();
            user.setEmail(txtEmail.getText());
            user.setPasswordHash(new String(txtPassword.getPassword()));

            User userAut = userDAO.authenticate(user);
            if (userAut != null && userAut.getId() > 0 && userAut.getEmail().equals((user.getEmail()))) {
                this.mainForm.setUserAutenticate(userAut);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Email y password incorrecto",
                        "Login",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Sistem",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
