package org.presentacion;

import org.dominio.Gasto;
import org.persistencia.GastoDAO; // Usaremos el DAO de Gasto
import org.utils.CUD;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Date; // Usaremos java.util.Date para el JSpinner

public class GastoWriteForm extends JDialog {
    private JPanel mainPanel;
    private JSpinner spinnerMonto;
    private JTextField txtCategoria;
    private JSpinner spinnerFecha;
    private JTextArea txtaDescripcion; // Usamos un JTextArea para descripciones más largas
    private JButton btnOk;
    private JButton btnCancel;
    private JTextField txtId;
    private JTextField txtUserId;
    private JLabel lbTituloAccion; // Un label para indicar la acción

    private GastoDAO gastoDAO;
    private MainForm mainForm;
    private CUD cud;
    private Gasto gasto; // La entidad ahora es un Gasto

    public GastoWriteForm(MainForm mainForm, CUD cud, Gasto gasto) {
        this.cud = cud;
        this.gasto = gasto;
        this.mainForm = mainForm;
        gastoDAO = new GastoDAO(); // Instanciamos el DAO de Gasto
        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setPreferredSize(new java.awt.Dimension(450, 400)); // Ajustamos el tamaño
        setLocationRelativeTo(mainForm);

        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void init() {
        // Configurar los spinners
        spinnerMonto.setModel(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 0.50));
        spinnerFecha.setModel(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));

        switch (this.cud) {
            case CREATE:
                setTitle("Registrar Nuevo Gasto");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Gasto");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Gasto");
                btnOk.setText("Eliminar");
                btnOk.setBackground(java.awt.Color.RED); // Resaltar el botón de eliminar
                break;
        }

        setValuesControls(this.gasto);
    }

    // Carga los datos del objeto Gasto en los controles del formulario
    private void setValuesControls(Gasto gasto) {
        // Si es un gasto nuevo, el monto es 0. Si no, es el monto del gasto.
        spinnerMonto.setValue(gasto.getMonto() != null ? gasto.getMonto() : BigDecimal.ZERO);
        txtCategoria.setText(gasto.getCategoria());
        txtaDescripcion.setText(gasto.getDescripcion());

        // El JSpinner usa java.util.Date, si la fecha no es nula, la asignamos. Si no, usamos la fecha actual.
        spinnerFecha.setValue(gasto.getFecha() != null ? new Date(gasto.getFecha().getTime()) : new Date());

        // Si la operación es Eliminar, deshabilitamos la edición.
        if (this.cud == CUD.DELETE) {
            spinnerMonto.setEnabled(false);
            txtCategoria.setEditable(false);
            spinnerFecha.setEnabled(false);
            txtaDescripcion.setEditable(false);
        }
    }

    // Valida y obtiene los valores de los controles para actualizar el objeto Gasto
    private boolean getValuesControls() {
        BigDecimal monto = new BigDecimal(spinnerMonto.getValue().toString());

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor que cero.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtCategoria.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La categoría es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (this.cud != CUD.CREATE && this.gasto.getId() == 0) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado un gasto válido para la operación.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Si la validación pasa, actualizamos el objeto Gasto
        this.gasto.setMonto(monto);
        this.gasto.setCategoria(txtCategoria.getText().trim());

        // Convertimos de java.util.Date (del spinner) a java.sql.Date (del modelo Gasto)
        Date utilDate = (Date) spinnerFecha.getValue();
        this.gasto.setFecha(new java.sql.Date(utilDate.getTime()));

        this.gasto.setDescripcion(txtaDescripcion.getText().trim());

        // MUY IMPORTANTE: Asignar el ID del usuario.
        // Aquí asumimos que tienes una forma de saber qué usuario está logueado.
        // Por ejemplo, obteniéndolo desde el MainForm.
        // this.gasto.setUserId(mainForm.getUsuarioActual().getId());

        // Para este ejemplo, lo dejaremos como un valor fijo (ej: 1) si es un gasto nuevo.
        if (this.cud == CUD.CREATE) {
            this.gasto.setUserId(1); // ¡DEBES CAMBIAR ESTO POR LA LÓGICA REAL!
        }

        return true;
    }

    // Lógica del botón principal (OK/Guardar/Eliminar)
    private void ok() {
        try {
            // No se necesita validar para eliminar, solo confirmar
            if (this.cud != CUD.DELETE && !getValuesControls()) {
                return; // Si getValuesControls() devuelve false, la validación falló.
            }

            boolean r = false;

            switch (this.cud) {
                case CREATE:
                    Gasto nuevoGasto = gastoDAO.create(this.gasto);
                    if (nuevoGasto.getId() > 0) r = true;
                    break;
                case UPDATE:
                    r = gastoDAO.update(this.gasto);
                    break;
                case DELETE:
                    // Ahora le pasamos solo el ID del gasto, que es lo que el método espera.
                    r = gastoDAO.delete(this.gasto.getId());
                    break;
            }

            if (r) {
                JOptionPane.showMessageDialog(this, "Transacción realizada exitosamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Cierra el formulario
            } else {
                JOptionPane.showMessageDialog(this, "No se logró realizar la acción en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}