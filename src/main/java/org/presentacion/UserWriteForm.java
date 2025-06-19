package org.presentacion;

import org.persistencia.UserDAO;
import org.utils.CBOption;

import javax.swing.*;

import org.dominio.User;
import org.utils.CUD;

public class UserWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox cbStatus;
    private JButton btnOk;
    private JButton btnCancel;
    private JLabel lbPassword;

    private UserDAO userDAO;
    private MainForm mainForm;
    private CUD cud;
    private User en;

    public UserWriteForm(MainForm mainForm, CUD cud, User user) {
        this.cud = cud;
        this.en = user;
        this.mainForm = mainForm;
        userDAO = new UserDAO();
        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(mainForm);
        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void init() {
        initCBStatus();

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Usuario");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Usuario");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Usuario");
                btnOk.setText("Eliminar");
                break;
        }

        setValuesControls(this.en);
    }

    private void initCBStatus() {
        DefaultComboBoxModel<CBOption> model = (DefaultComboBoxModel<CBOption>) cbStatus.getModel();

        model.addElement(new CBOption("ACTIVO", (byte) 1));

        model.addElement(new CBOption("INACTIVO", (byte) 2));
    }

    private void setValuesControls(User user) {
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        cbStatus.setSelectedItem(new CBOption(null, user.getStatus()));

        if (this.cud == CUD.CREATE) {
            cbStatus.setSelectedItem(new CBOption(null, 1));
        }

        if (this.cud == CUD.DELETE) {
            txtName.setEditable(false);
            txtEmail.setEditable(false);
            cbStatus.setEnabled(false);
        }

        if (this.cud != CUD.CREATE) {
            txtPassword.setVisible(false);
            lbPassword.setVisible(false);
        }
    }

    private boolean getValuesControls() {
        boolean res = false;

        CBOption selectedOption = (CBOption) cbStatus.getSelectedItem();
        byte status = selectedOption != null ? (byte) (selectedOption.getValue()) : (byte) 0;

        if (txtName.getText().trim().isEmpty()) {
            return res;
        } else if (txtEmail.getText().trim().isEmpty()) {
            return res;
        } else if (status == (byte) 0) {
            return res;
        }
        else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            return res;
        }

        res = true;

        this.en.setName(txtName.getText());
        this.en.setEmail(txtEmail.getText());
        this.en.setStatus(status);

        if (this.cud == CUD.CREATE) {
            this.en.setPasswordHash(new String(txtPassword.getPassword()));
            if (this.en.getPasswordHash().trim().isEmpty()){
                return  false;
            }
        }

        return res;
    }

    private void ok() {
        try {
            boolean res = getValuesControls();

            if (res) {
                boolean r = false;

                switch (this.cud) {
                    case CREATE:
                        User user = userDAO.create(this.en);
                        if (user.getId() > 0) {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = userDAO.update(this.en);
                        break;
                    case DELETE:
                        r = userDAO.delete(this.en);
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Los campos con son obligatorios",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}

