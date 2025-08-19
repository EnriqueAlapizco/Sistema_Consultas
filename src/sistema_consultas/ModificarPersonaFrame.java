/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ModificarPersonaFrame extends JFrame {

    // Campos
    private final JTextField txtId = new JTextField(6);
    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtDireccion = new JTextField(25);
    private final JTextField txtTelefono = new JTextField(15);

    private final DefaultListModel<String> modeloTelefonos = new DefaultListModel<>();
    private final JList<String> lstTelefonos = new JList<>(modeloTelefonos);

    private final JButton btnCargar = new JButton("Cargar por ID");
    private final JButton btnAgregarTelefono = new JButton("Agregar teléfono");
    private final JButton btnQuitarTelefono = new JButton("Quitar seleccionado");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final PersonaDAO dao = new PersonaDAO();

    public ModificarPersonaFrame() {
        setTitle("Modificar Persona");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: ID + botón cargar
        JPanel pnlId = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlId.add(new JLabel("ID Persona:"));
        pnlId.add(txtId);
        pnlId.add(btnCargar);
        add(pnlId, BorderLayout.NORTH);

        // Panel centro: datos básicos + teléfonos
        JPanel pnlCentro = new JPanel(new BorderLayout(10, 10));

        JPanel pnlDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlDatos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnlDatos.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlDatos.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; pnlDatos.add(txtDireccion, gbc);

        pnlCentro.add(pnlDatos, BorderLayout.NORTH);

        JPanel pnlTelefonos = new JPanel(new BorderLayout(5, 5));
        JPanel pnlAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAdd.add(new JLabel("Teléfono:"));
        pnlAdd.add(txtTelefono);
        pnlAdd.add(btnAgregarTelefono);

        JPanel pnlBtnsTel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBtnsTel.add(btnQuitarTelefono);

        lstTelefonos.setVisibleRowCount(6);
        JScrollPane scroll = new JScrollPane(lstTelefonos);

        pnlTelefonos.add(pnlAdd, BorderLayout.NORTH);
        pnlTelefonos.add(scroll, BorderLayout.CENTER);
        pnlTelefonos.add(pnlBtnsTel, BorderLayout.SOUTH);
        pnlTelefonos.setBorder(BorderFactory.createTitledBorder("Teléfonos (se reemplazarán)"));

        pnlCentro.add(pnlTelefonos, BorderLayout.CENTER);

        add(pnlCentro, BorderLayout.CENTER);

        // Panel inferior: acciones
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAcciones.add(btnLimpiar);
        pnlAcciones.add(btnActualizar);
        add(pnlAcciones, BorderLayout.SOUTH);

        // Listeners
        btnCargar.addActionListener(this::onCargar);
        btnAgregarTelefono.addActionListener(this::onAgregarTelefono);
        btnQuitarTelefono.addActionListener(this::onQuitarTelefono);
        btnActualizar.addActionListener(this::onActualizar);
        btnLimpiar.addActionListener(e -> limpiar());

        pack();
        setLocationRelativeTo(null);
    }

    private void onCargar(ActionEvent e) {
        int id;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1) Cargar nombre/dirección
        String[] basicos = dao.obtenerPersonaBasicaPorId(id);
        if (basicos == null) {
            JOptionPane.showMessageDialog(this, "No se encontró persona con ese ID", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtNombre.setText(basicos[0]);
        txtDireccion.setText(basicos[1]);

        // 2) Cargar teléfonos
        modeloTelefonos.clear();
        for (String tel : dao.obtenerTelefonosPorPersona(id)) {
            modeloTelefonos.addElement(tel);
        }
    }

    private void onAgregarTelefono(ActionEvent e) {
        String tel = txtTelefono.getText().trim();
        if (tel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un teléfono", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloTelefonos.addElement(tel);
        txtTelefono.setText("");
        txtTelefono.requestFocus();
    }

    private void onQuitarTelefono(ActionEvent e) {
        int idx = lstTelefonos.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un teléfono para quitar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloTelefonos.remove(idx);
    }

    private void onActualizar(ActionEvent e) {
        int id;
        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        List<String> telefonos = new ArrayList<>();
        for (int i = 0; i < modeloTelefonos.size(); i++) {
            String t = modeloTelefonos.get(i).trim();
            if (!t.isEmpty()) telefonos.add(t);
        }

        boolean ok = dao.actualizarPersonaYTelefonos(id, nombre, direccion, telefonos);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Datos actualizados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar (¿ID inexistente?)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        modeloTelefonos.clear();
        txtId.requestFocus();
    }

   // public static void main(String[] args) {
       // SwingUtilities.invokeLater(() -> {
           // try {
               // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           // } catch (Exception ignored) {}
           // new ModificarPersonaFrame().setVisible(true);
        // });
   // }
}