package org.presentacion;

import org.dominio.Gasto;
import org.persistencia.GastoDAO;
import org.utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

public class GastoReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtSearch;
    private JButton btnCreate;
    private JTable tableGastos;
    private JButton btnUpdate;
    private JButton btnDelete;

    private GastoDAO gastoDAO;
    private MainForm mainForm;

    public GastoReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        gastoDAO = new GastoDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Gastos");
        setPreferredSize(new java.awt.Dimension(750, 550));
        pack();
        setLocationRelativeTo(mainForm);

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

        btnCreate.addActionListener(e -> {
            GastoWriteForm gastoWriteForm = new GastoWriteForm(this.mainForm, CUD.CREATE, new Gasto());
            gastoWriteForm.setVisible(true);
            refreshTable();
        });

        btnUpdate.addActionListener(e -> {
            Gasto gasto = getGastoFromTableRow();
            if (gasto != null) {
                GastoWriteForm gastoWriteForm = new GastoWriteForm(this.mainForm, CUD.UPDATE, gasto);
                gastoWriteForm.setVisible(true);
                refreshTable();
            }
        });

        btnDelete.addActionListener(e -> {
            Gasto gasto = getGastoFromTableRow();
            if (gasto != null) {
                GastoWriteForm gastoWriteForm = new GastoWriteForm(this.mainForm, CUD.DELETE, gasto);
                gastoWriteForm.setVisible(true);
                refreshTable();
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        try {
            List<Gasto> gastos = gastoDAO.getAll();
            createTable(gastos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar gastos: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void search(String query) {
        try {
            List<Gasto> gastos = gastoDAO.search(query);
            createTable(gastos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar gastos: " + ex.getMessage(),
                    "ERROR de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void createTable(List<Gasto> gastos) {
        String[] columnNames = {"Id", "UserId", "Monto", "Categoría", "Fecha", "Descripción"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tableGastos.setModel(model);

        for (Gasto gasto : gastos) {
            Object[] rowData = new Object[]{
                    gasto.getId(),
                    gasto.getUserId(),
                    gasto.getMonto(),
                    gasto.getCategoria(),
                    gasto.getFecha(),
                    gasto.getDescripcion()
            };
            model.addRow(rowData);
        }
    }

    private Gasto getGastoFromTableRow() {
        Gasto gasto = null;
        try {
            int filaSelect = this.tableGastos.getSelectedRow();
            if (filaSelect != -1) {
                int id = (int) this.tableGastos.getModel().getValueAt(filaSelect, 0);
                gasto = gastoDAO.getById(id);
                if (gasto == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún gasto para el ID seleccionado.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
            return gasto;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener gasto de la fila seleccionada: " + ex.getMessage(),
                    "ERROR de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return null;
        }
    }
}
