/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ModificarPersonaFrame extends JFrame {

    private JTextField txtId, txtNombre, txtTelefono;
    private JButton btnCargar, btnGuardar, btnAgregarTel, btnQuitarTel, btnAgregarDir, btnQuitarDir;
    private DefaultListModel<String> modeloTelefonos = new DefaultListModel<>();
    private JList<String> lstTelefonos = new JList<>(modeloTelefonos);

    private DefaultListModel<DireccionItem> modeloDirecciones = new DefaultListModel<>();
    private JList<DireccionItem> lstDirecciones = new JList<>(modeloDirecciones);
    private JTextField txtDireccion;

    private PersonaDAO dao = new PersonaDAO();

    // Clase interna para manejar ID + calle
    private static class DireccionItem {
        int id;
        String calle;
        public DireccionItem(int id, String calle) { this.id = id; this.calle = calle; }
        @Override public String toString() { return calle; }
    }

    public ModificarPersonaFrame() {
        setTitle("Modificar Persona");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Panel superior: ID + boton cargar
        JPanel pnlArriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlArriba.add(new JLabel("ID:"));
        txtId = new JTextField(5);
        pnlArriba.add(txtId);
        btnCargar = new JButton("Cargar");
        pnlArriba.add(btnCargar);
        add(pnlArriba, BorderLayout.NORTH);

        // Panel central: Nombre, telefonos y direcciones
        JPanel pnlCentro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; pnlCentro.add(new JLabel("Nombre:"), gbc);
        gbc.gridx=1; gbc.gridy=0; txtNombre = new JTextField(20); pnlCentro.add(txtNombre, gbc);

        // Telefonos
        gbc.gridx=0; gbc.gridy=1; gbc.gridwidth=2;
        JPanel pnlTel = new JPanel(new BorderLayout(5,5));
        pnlTel.setBorder(BorderFactory.createTitledBorder("Teléfonos"));
        JPanel pnlAddTel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTelefono = new JTextField(10);
        btnAgregarTel = new JButton("Agregar");
        btnQuitarTel = new JButton("Quitar");
        pnlAddTel.add(txtTelefono); pnlAddTel.add(btnAgregarTel); pnlAddTel.add(btnQuitarTel);
        pnlTel.add(pnlAddTel, BorderLayout.NORTH);
        lstTelefonos.setVisibleRowCount(5);
        pnlTel.add(new JScrollPane(lstTelefonos), BorderLayout.CENTER);
        pnlCentro.add(pnlTel, gbc);

        // Direcciones
        gbc.gridy=2;
        JPanel pnlDir = new JPanel(new BorderLayout(5,5));
        pnlDir.setBorder(BorderFactory.createTitledBorder("Direcciones"));
        JPanel pnlAddDir = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtDireccion = new JTextField(15);
        btnAgregarDir = new JButton("Agregar");
        btnQuitarDir = new JButton("Quitar");
        pnlAddDir.add(txtDireccion); pnlAddDir.add(btnAgregarDir); pnlAddDir.add(btnQuitarDir);
        pnlDir.add(pnlAddDir, BorderLayout.NORTH);
        lstDirecciones.setVisibleRowCount(5);
        pnlDir.add(new JScrollPane(lstDirecciones), BorderLayout.CENTER);
        pnlCentro.add(pnlDir, gbc);

        add(pnlCentro, BorderLayout.CENTER);

        // Panel inferior: guardar
        JPanel pnlAbajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar cambios");
        pnlAbajo.add(btnGuardar);
        add(pnlAbajo, BorderLayout.SOUTH);

        // Listeners
        btnCargar.addActionListener(e -> cargarDatos());
        btnAgregarTel.addActionListener(e -> agregarTelefono());
        btnQuitarTel.addActionListener(e -> quitarTelefono());
        btnAgregarDir.addActionListener(e -> agregarDireccion());
        btnQuitarDir.addActionListener(e -> quitarDireccion());
        btnGuardar.addActionListener(e -> guardarCambios());

        pack();
        setLocationRelativeTo(null);
    }

    private void cargarDatos() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String[] datos = dao.obtenerPersonaBasicaPorId(id);
            if (datos == null) {
                JOptionPane.showMessageDialog(this, "No se encontró persona con ese ID");
                return;
            }
            txtNombre.setText(datos[0]);

            // Cargar teléfonos
            modeloTelefonos.clear();
            List<String> telefonos = dao.obtenerTelefonosPorPersona(id);
            for (String t : telefonos) modeloTelefonos.addElement(t);

            // Cargar direcciones
            modeloDirecciones.clear();
            List<String> direcciones = dao.obtenerDireccionesPorPersona(id);
            // Aquí creamos DireccionItem con ID ficticio de -1 para nuevas
            int cont = 1;
            for (String dir : direcciones) modeloDirecciones.addElement(new DireccionItem(cont++, dir));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido");
        }
    }

    private void agregarTelefono() {
        String t = txtTelefono.getText().trim();
        if (!t.isEmpty()) { modeloTelefonos.addElement(t); txtTelefono.setText(""); }
    }

    private void quitarTelefono() {
        int idx = lstTelefonos.getSelectedIndex();
        if (idx != -1) modeloTelefonos.remove(idx);
    }

    private void agregarDireccion() {
        String d = txtDireccion.getText().trim();
        if (!d.isEmpty()) { 
            modeloDirecciones.addElement(new DireccionItem(-1, d)); 
            txtDireccion.setText(""); 
        }
    }

    private void quitarDireccion() {
        int idx = lstDirecciones.getSelectedIndex();
        if (idx != -1) modeloDirecciones.remove(idx);
    }

    private void guardarCambios() {
    try {
        int id = Integer.parseInt(txtId.getText().trim());
        String nombre = txtNombre.getText().trim();

        // Telefonos
        List<String> telefonos = new ArrayList<>();
        for (int i = 0; i < modeloTelefonos.size(); i++) {
            telefonos.add(modeloTelefonos.get(i));
        }

        // Direcciones
        List<Integer> direccionIds = new ArrayList<>();
        List<String> nuevasDirecciones = new ArrayList<>();

        for (int i = 0; i < modeloDirecciones.size(); i++) {
            DireccionItem di = modeloDirecciones.get(i);
            if (di.id > 0) {
                direccionIds.add(di.id);
            } else {
                direccionIds.add(null);
                nuevasDirecciones.add(di.calle);
            }
        }

        boolean ok = dao.actualizarPersona(id, nombre, telefonos, direccionIds, nuevasDirecciones);
        if (ok) JOptionPane.showMessageDialog(this, "Cambios guardados correctamente");
        else JOptionPane.showMessageDialog(this, "No se pudieron guardar los cambios");

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "ID inválido");
    }
}

    //public static void main(String[] args) {
        //SwingUtilities.invokeLater(() -> new ModificarPersonaFrame().setVisible(true));
    //}
}