package org;

import org.presentacion.LoginForm;
import org.presentacion.MainForm;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MainForm mainForm = new MainForm();
            mainForm.setVisible(true);
            LoginForm loginForm = new LoginForm(mainForm);
            loginForm.setVisible(true);
        });
    }
}