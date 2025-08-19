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

public class AltaPersonaFrame extends JFrame {

    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtDireccion = new JTextField(25);
    private final JTextField txtTelefono = new JTextField(15);

    private final DefaultListModel<String> modeloTelefonos = new DefaultListModel<>();
    private final JList<String> lstTelefonos = new JList<>(modeloTelefonos);

    private final JButton btnAgregarTelefono = new JButton("Agregar teléfono");
    private final JButton btnQuitarTelefono = new JButton("Quitar seleccionado");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final PersonaDAO dao = new PersonaDAO();

    public AltaPersonaFrame() {
        setTitle("Altas — Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: datos básicos
        JPanel pnlDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlDatos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnlDatos.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlDatos.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; pnlDatos.add(txtDireccion, gbc);

        add(pnlDatos, BorderLayout.NORTH);

        // Panel central: teléfonos
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

        pnlTelefonos.setBorder(BorderFactory.createTitledBorder("Teléfonos (0..N)"));

        add(pnlTelefonos, BorderLayout.CENTER);

        // Panel inferior: acciones
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAcciones.add(btnLimpiar);
        pnlAcciones.add(btnGuardar);
        add(pnlAcciones, BorderLayout.SOUTH);

        // Listeners
        btnAgregarTelefono.addActionListener(this::onAgregarTelefono);
        btnQuitarTelefono.addActionListener(this::onQuitarTelefono);
        btnGuardar.addActionListener(this::onGuardar);
        btnLimpiar.addActionListener(e -> limpiar());

        pack();
        setLocationRelativeTo(null);
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

    private void onGuardar(ActionEvent e) {
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

        boolean ok = dao.agregarPersona(nombre, direccion, telefonos);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Persona guardada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar. Revisa la consola para más detalles.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        modeloTelefonos.clear();
        txtNombre.requestFocus();
    }

   // public static void main(String[] args) {
       // SwingUtilities.invokeLater(() -> {
           // try {
               // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           // } catch (Exception ignored) {}
           // new AltaPersonaFrame().setVisible(true);
       // });
   // }
    
    
}
