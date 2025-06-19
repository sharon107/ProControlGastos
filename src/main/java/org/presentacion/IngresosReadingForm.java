package org.presentacion;

import org.dominio.Ingresos;
import org.persistencia.IngresosDAO;
import org.utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

public class IngresosReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtSearch;
    private JButton btnCreate;
    private JTable tableIngresos;
    private JButton btnUpdate;
    private JButton btnDelete;

    private IngresosDAO ingresosDAO;
    private MainForm mainForm;

    public IngresosReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        ingresosDAO = new IngresosDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Ingresos");
        setPreferredSize(new java.awt.Dimension(750, 550));
        pack();
        setLocationRelativeTo(mainForm);

        // Búsqueda en tiempo real
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtSearch.getText().trim().isEmpty()) {
                    search(txtSearch.getText());
                } else {
                    refreshTable();
                }
            }
        });

        // Crear ingreso
        btnCreate.addActionListener(e -> {
            IngresosWriteForm ingresosWriteForm = new IngresosWriteForm(this.mainForm, CUD.CREATE, new Ingresos());
            ingresosWriteForm.setVisible(true);
            refreshTable();
        });

        // Actualizar ingreso
        btnUpdate.addActionListener(e -> {
            Ingresos ingreso = getIngresoFromTableRow();
            if (ingreso != null) {
                IngresosWriteForm ingresosWriteForm = new IngresosWriteForm(this.mainForm, CUD.UPDATE, ingreso);
                ingresosWriteForm.setVisible(true);
                refreshTable();
            }
        });

        // Eliminar ingreso
        btnDelete.addActionListener(e -> {
            Ingresos ingreso = getIngresoFromTableRow();
            if (ingreso != null) {
                IngresosWriteForm ingresosWriteForm = new IngresosWriteForm(this.mainForm, CUD.DELETE, ingreso);
                ingresosWriteForm.setVisible(true);
                refreshTable();
            }
        });

        // Cargar ingresos al abrir
        refreshTable();
    }

    private void refreshTable() {
        try {
            List<Ingresos> ingresos = ingresosDAO.getAll();
            createTable(ingresos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar ingresos: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void search(String query) {
        try {
            List<Ingresos> ingresos = ingresosDAO.search(query);
            createTable(ingresos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar ingresos: " + ex.getMessage(),
                    "ERROR de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void createTable(List<Ingresos> ingresos) {
        // Mostrar todos los campos, incluyendo Id y UserId
        String[] columnNames = {"Id", "UserId", "Monto", "Fuente", "Fecha", "Descripción"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tableIngresos.setModel(model);

        for (Ingresos ingreso : ingresos) {
            Object[] rowData = new Object[]{
                    ingreso.getId(),
                    ingreso.getUserId(),
                    ingreso.getMonto(),
                    ingreso.getFuente(),
                    ingreso.getFecha(),
                    ingreso.getDescripcion()
            };
            model.addRow(rowData);
        }
    }

    private Ingresos getIngresoFromTableRow() {
        Ingresos ingreso = null;
        try {
            int filaSelect = this.tableIngresos.getSelectedRow();
            if (filaSelect != -1) {
                int id = (int) this.tableIngresos.getModel().getValueAt(filaSelect, 0);
                ingreso = ingresosDAO.getById(id);
                if (ingreso == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún ingreso para el ID seleccionado.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
            return ingreso;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener ingreso de la fila seleccionada: " + ex.getMessage(),
                    "ERROR de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return null;
        }
    }
}
