package org.presentacion;

import javax.swing.*;
import org.dominio.User;

public class MainForm extends JFrame {

    private User userAutenticate;

    public User getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(User userAutenticate) {
        this.userAutenticate = userAutenticate;
    }

    public MainForm() {
        setTitle("Sistema en java de escritorio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        createMenu();
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menú Perfil
        JMenu menuPerfil = new JMenu("Perfil");
        menuBar.add(menuPerfil);

        JMenuItem itemChangePassword = new JMenuItem("Cambiar contraseña");
        menuPerfil.add(itemChangePassword);
        itemChangePassword.addActionListener(e -> {
            ChangePasswordForm changePassword = new ChangePasswordForm(this);
            changePassword.setVisible(true);
        });

        JMenuItem itemChangeUser = new JMenuItem("Cambiar de usuario");
        menuPerfil.add(itemChangeUser);
        itemChangeUser.addActionListener(e -> {
            LoginForm loginForm = new LoginForm(this);
            loginForm.setVisible(true);
        });

        JMenuItem itemSalir = new JMenuItem("Salir");
        menuPerfil.add(itemSalir);
        itemSalir.addActionListener(e -> System.exit(0));

        // Menú Mantenimientos
        JMenu menuMantenimiento = new JMenu("Mantenimientos");
        menuBar.add(menuMantenimiento);

        JMenuItem itemUsers = new JMenuItem("Usuarios");
        menuMantenimiento.add(itemUsers);
        itemUsers.addActionListener(e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });

        // NUEVO: Menú Finanzas con Ingresos y Gastos
        JMenu menuFinanzas = new JMenu("Finanzas");
        menuBar.add(menuFinanzas);

        JMenuItem itemIngresos = new JMenuItem("Ingresos");
        menuFinanzas.add(itemIngresos);
        itemIngresos.addActionListener(e -> {
            IngresosReadingForm ingresosForm = new IngresosReadingForm(this);
            ingresosForm.setVisible(true);
        });

        JMenuItem itemGastos = new JMenuItem("Gastos");
        menuFinanzas.add(itemGastos);
        itemGastos.addActionListener(e -> {
            GastoReadingForm gastoForm = new GastoReadingForm(this);
            gastoForm.setVisible(true);
        });
    }
}
