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
    private final DefaultListModel<String> modeloDirecciones = new DefaultListModel<>();
    private final JList<String> lstDirecciones = new JList<>(modeloDirecciones);

    private final JTextField txtTelefono = new JTextField(15);
    private final DefaultListModel<String> modeloTelefonos = new DefaultListModel<>();
    private final JList<String> lstTelefonos = new JList<>(modeloTelefonos);

    private final JButton btnAgregarDir = new JButton("Agregar dirección");
    private final JButton btnQuitarDir = new JButton("Quitar seleccionada");
    private final JButton btnAgregarTel = new JButton("Agregar teléfono");
    private final JButton btnQuitarTel = new JButton("Quitar seleccionado");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final PersonaDAO dao = new PersonaDAO();

    public AltaPersonaFrame() {
        setTitle("Altas — Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Panel superior: nombre
        JPanel pnlNombre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNombre.add(new JLabel("Nombre:"));
        pnlNombre.add(txtNombre);
        add(pnlNombre, BorderLayout.NORTH);

        // Panel central: direcciones y teléfonos
        JPanel pnlCentro = new JPanel(new GridLayout(2,1,10,10));

        // Direcciones
        JPanel pnlDir = new JPanel(new BorderLayout());
        JPanel pnlAddDir = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAddDir.add(new JLabel("Dirección:"));
        pnlAddDir.add(txtDireccion);
        pnlAddDir.add(btnAgregarDir);
        pnlDir.add(pnlAddDir, BorderLayout.NORTH);
        pnlDir.add(new JScrollPane(lstDirecciones), BorderLayout.CENTER);
        pnlDir.add(btnQuitarDir, BorderLayout.SOUTH);
        pnlDir.setBorder(BorderFactory.createTitledBorder("Direcciones (1..N)"));
        pnlCentro.add(pnlDir);

        // Teléfonos
        JPanel pnlTel = new JPanel(new BorderLayout());
        JPanel pnlAddTel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAddTel.add(new JLabel("Teléfono:"));
        pnlAddTel.add(txtTelefono);
        pnlAddTel.add(btnAgregarTel);
        pnlTel.add(pnlAddTel, BorderLayout.NORTH);
        pnlTel.add(new JScrollPane(lstTelefonos), BorderLayout.CENTER);
        pnlTel.add(btnQuitarTel, BorderLayout.SOUTH);
        pnlTel.setBorder(BorderFactory.createTitledBorder("Teléfonos (0..N)"));
        pnlCentro.add(pnlTel);

        add(pnlCentro, BorderLayout.CENTER);

        // Panel inferior: acciones
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAcciones.add(btnLimpiar);
        pnlAcciones.add(btnGuardar);
        add(pnlAcciones, BorderLayout.SOUTH);

        // Listeners
        btnAgregarDir.addActionListener(this::onAgregarDireccion);
        btnQuitarDir.addActionListener(e -> quitarSeleccion(lstDirecciones, modeloDirecciones));
        btnAgregarTel.addActionListener(this::onAgregarTelefono);
        btnQuitarTel.addActionListener(e -> quitarSeleccion(lstTelefonos, modeloTelefonos));
        btnGuardar.addActionListener(this::onGuardar);
        btnLimpiar.addActionListener(e -> limpiar());

        pack();
        setLocationRelativeTo(null);
    }

    private void onAgregarDireccion(ActionEvent e) {
        String dir = txtDireccion.getText().trim();
        if (!dir.isEmpty()) {
            modeloDirecciones.addElement(dir);
            txtDireccion.setText("");
        }
    }

    private void onAgregarTelefono(ActionEvent e) {
        String tel = txtTelefono.getText().trim();
        if (!tel.isEmpty()) {
            modeloTelefonos.addElement(tel);
            txtTelefono.setText("");
        }
    }

    private void quitarSeleccion(JList<String> list, DefaultListModel<String> model) {
        int idx = list.getSelectedIndex();
        if (idx != -1) model.remove(idx);
    }

    private void onGuardar(ActionEvent e) {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,"El nombre es obligatorio","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> direcciones = new ArrayList<>();
        for (int i=0;i<modeloDirecciones.size();i++) direcciones.add(modeloDirecciones.get(i));
        if (direcciones.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Agrega al menos una dirección","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> telefonos = new ArrayList<>();
        for (int i=0;i<modeloTelefonos.size();i++) telefonos.add(modeloTelefonos.get(i));

        boolean ok = dao.agregarPersona(nombre,direcciones,telefonos);
        if (ok) {
            JOptionPane.showMessageDialog(this,"Persona guardada correctamente","Éxito",JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this,"No se pudo guardar. Revisa la consola.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        modeloDirecciones.clear();
        modeloTelefonos.clear();
    }

    // --- Main local para pruebas ---
    //public static void main(String[] args) {
        
        //SwingUtilities.invokeLater(() -> new AltaPersonaFrame().setVisible(true));
    //}
}