package org.presentacion;

import org.persistencia.UserDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.dominio.User;
import org.utils.CUD;
import java.lang.NullPointerException;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class UserReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableUsers;
    private JButton btnUpdate;
    private JButton btnDelete;

    private UserDAO userDAO;
    private MainForm mainForm;

    public UserReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UserDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Usuarios");
        setPreferredSize(new java.awt.Dimension(750, 550));
        pack();
        setLocationRelativeTo(mainForm);

        // Listener para búsqueda en tiempo real
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    refreshTable();
                }
            }
        });

        // Botón Crear
        btnCreate.addActionListener(s -> {
            UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.CREATE, new User());
            userWriteForm.setVisible(true);
            refreshTable();
        });

        // Botón Actualizar
        btnUpdate.addActionListener(s -> {
            User user = getUserFromTableRow();
            if (user != null) {
                UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.UPDATE, user);
                userWriteForm.setVisible(true);
                refreshTable();
            }
        });

        // Botón Eliminar
        btnDelete.addActionListener(s -> {
            User user = getUserFromTableRow();
            if (user != null) {
                UserWriteForm userWriteForm = new UserWriteForm(this.mainForm, CUD.DELETE, user);
                userWriteForm.setVisible(true);
                refreshTable();
            }
        });

        // Cargar usuarios al abrir
        refreshTable();
    }

    // 🔧 Método agregado para corregir error
    private void refreshTable() {
        try {
            List<User> users = userDAO.getAll();
            createTable(users);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void search(String query) {
        try {
            List<User> users = userDAO.search(query);
            createTable(users);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar usuarios: " + ex.getMessage(), "ERROR de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void createTable(List<User> users) {
        String[] columnNames = {"Id", "Nombre", "Email", "Estatus"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tableUsers.setModel(model);

        for (User user : users) {
            Object[] rowData = new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getStrEstatus() // Asegúrate que existe este método
            };
            model.addRow(rowData);
        }

        hideCol(0);
    }

    private void hideCol(int columnIndex) {
        this.tableUsers.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        this.tableUsers.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        this.tableUsers.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        this.tableUsers.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
    }

    private User getUserFromTableRow() {
        User user = null;
        try {
            int filaSelect = this.tableUsers.getSelectedRow();
            if (filaSelect != -1) {
                int id = (int) this.tableUsers.getModel().getValueAt(filaSelect, 0);
                user = userDAO.getById(id);
                if (user == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún usuario para el ID seleccionado.", "Validación", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila de la tabla.", "Validación", JOptionPane.WARNING_MESSAGE);
            }
            return user;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener usuario de la fila seleccionada: " + ex.getMessage(), "ERROR de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return null;
        }
    }
}


