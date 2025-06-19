package org.presentacion;

import org.dominio.Ingresos;
import org.persistencia.IngresosDAO; // Usaremos el DAO de Ingresos
import org.utils.CUD;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Date; // Usaremos java.util.Date para el JSpinner de fecha
import java.sql.SQLException; // Para manejar las excepciones de SQL

public class IngresosWriteForm extends JDialog {
    private JPanel mainPanel;
    private JSpinner spinnerMonto;
    private JTextField txtFuente; // Para el campo 'Fuente'
    private JSpinner spinnerFecha;
    private JTextArea txtaDescripcion; // Para 'Descripcion'
    private JButton btnOk;
    private JButton btnCancel;
    private JTextField txtId;
    private JTextField txtUserId;
    private JLabel lbTituloAccion; // Etiqueta para el título de la acción

    private IngresosDAO ingresosDAO;
    private MainForm mainForm;
    private CUD cud;
    private Ingresos ingreso; // La entidad ahora es un Ingresos

    public IngresosWriteForm(MainForm mainForm, CUD cud, Ingresos ingreso) { // Similar a GastoWriteForm [22]
        this.cud = cud;
        this.ingreso = ingreso;
        this.mainForm = mainForm;
        ingresosDAO = new IngresosDAO(); // Instanciamos el DAO de Ingresos
        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setPreferredSize(new java.awt.Dimension(450, 400)); // Ajustamos el tamaño
        setLocationRelativeTo(mainForm);

        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void init() { // Similar a GastoWriteForm.init [23]
        // Configurar los spinners
        spinnerMonto.setModel(new SpinnerNumberModel(0.0, 0.0, 10000000.0, 0.50)); // Rango de monto adecuado para ingresos
        spinnerFecha.setModel(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));

        // Configuración de la interfaz según la operación CUD
        switch (this.cud) {
            case CREATE:
                setTitle("Registrar Nuevo Ingreso");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Ingreso");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Ingreso");
                btnOk.setText("Eliminar");
                btnOk.setBackground(java.awt.Color.RED); // Resaltar el botón de eliminar
                break;
        }

        setValuesControls(this.ingreso);
    }

    // Carga los datos del objeto Ingresos en los controles del formulario, similar a GastoWriteForm.setValuesControls [23]
    private void setValuesControls(Ingresos ingreso) {
        // Si es un ingreso nuevo, el monto es 0. Si no, es el monto del ingreso.
        spinnerMonto.setValue(ingreso.getMonto() != null ? ingreso.getMonto() : BigDecimal.ZERO);
        txtFuente.setText(ingreso.getFuente()); // Asignar el valor de 'Fuente'
        txtaDescripcion.setText(ingreso.getDescripcion());

        // El JSpinner usa java.util.Date, si la fecha no es nula, la asignamos. Si no, usamos la fecha actual.
        spinnerFecha.setValue(ingreso.getFecha() != null ? new Date(ingreso.getFecha().getTime()) : new Date());

        // Si la operación es Eliminar, deshabilitamos la edición.
        if (this.cud == CUD.DELETE) {
            spinnerMonto.setEnabled(false);
            txtFuente.setEditable(false);
            spinnerFecha.setEnabled(false);
            txtaDescripcion.setEditable(false);
        }
    }

    // Valida y obtiene los valores de los controles para actualizar el objeto Ingresos, similar a GastoWriteForm.getValuesControls [24]
    private boolean getValuesControls() {
        BigDecimal monto = new BigDecimal(spinnerMonto.getValue().toString());

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor que cero.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtFuente.getText().trim().isEmpty()) { // Validar la fuente
            JOptionPane.showMessageDialog(this, "La fuente es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (this.cud != CUD.CREATE && this.ingreso.getId() == 0) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado un ingreso válido para la operación.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Si la validación pasa, actualizamos el objeto Ingresos
        this.ingreso.setMonto(monto);
        this.ingreso.setFuente(txtFuente.getText().trim()); // Asignar la fuente

        // Convertimos de java.util.Date (del spinner) a java.sql.Date (del modelo Ingresos)
        Date utilDate = (Date) spinnerFecha.getValue();
        this.ingreso.setFecha(new java.sql.Date(utilDate.getTime()));

        this.ingreso.setDescripcion(txtaDescripcion.getText().trim());

        // MUY IMPORTANTE: Asignar el ID del usuario.
        // Aquí se asume que tienes una forma de saber qué usuario está logueado.
        // Por ejemplo, obteniéndolo desde el MainForm, similar a la nota en GastoWriteForm [24].
        if (this.cud == CUD.CREATE) {
            this.ingreso.setUserId(1); // ¡DEBES CAMBIAR ESTO POR LA LÓGICA REAL DE OBTENCIÓN DEL USUARIO LOGUEADO!
        }

        return true;
    }

    // Lógica del botón principal (OK/Guardar/Eliminar), similar a GastoWriteForm.ok [25]
    private void ok() {
        try {
            // No se necesita validar para eliminar, solo confirmar
            if (this.cud != CUD.DELETE && !getValuesControls()) {
                return; // Si getValuesControls() devuelve false, la validación falló.
            }

            boolean r = false;

            switch (this.cud) {
                case CREATE:
                    Ingresos nuevoIngreso = ingresosDAO.create(this.ingreso);
                    if (nuevoIngreso != null && nuevoIngreso.getId() > 0) r = true; // Asegurarse que se creó y tiene ID
                    break;
                case UPDATE:
                    r = ingresosDAO.update(this.ingreso);
                    break;
                case DELETE:
                    // Le pasamos solo el ID del ingreso, que es lo que el método espera.
                    r = ingresosDAO.delete(this.ingreso.getId()); // Similar a GastoWriteForm.ok [25]
                    break;
            }

            if (r) {
                JOptionPane.showMessageDialog(this, "Transacción realizada exitosamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Cierra el formulario
            } else {
                JOptionPane.showMessageDialog(this, "No se logró realizar la acción en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) { // Captura específica de SQLException
            JOptionPane.showMessageDialog(this, "Ocurrió un error de base de datos: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) { // Captura para otras excepciones inesperadas
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}