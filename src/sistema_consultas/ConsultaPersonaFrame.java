/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ConsultaPersonaFrame extends JFrame {
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextArea txtTelefonos;
    private JTextArea txtDirecciones;
    private JButton btnBuscar;
    private PersonaDAO dao;

    public ConsultaPersonaFrame() {
        dao = new PersonaDAO();
        setTitle("Consulta de Persona");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel topPanel = new JPanel(new GridLayout(2,2,5,5));

        topPanel.add(new JLabel("ID de la persona:"));
        txtId = new JTextField();
        topPanel.add(txtId);

        btnBuscar = new JButton("Buscar");
        topPanel.add(new JLabel());
        topPanel.add(btnBuscar);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3,1,5,5));
        txtNombre = new JTextField();
        txtNombre.setEditable(false);
        txtTelefonos = new JTextArea();
        txtTelefonos.setEditable(false);
        txtDirecciones = new JTextArea();
        txtDirecciones.setEditable(false);

        centerPanel.add(labeledPanel("Nombre:", txtNombre));
        centerPanel.add(labeledPanel("Teléfonos:", new JScrollPane(txtTelefonos)));
        centerPanel.add(labeledPanel("Direcciones:", new JScrollPane(txtDirecciones)));

        panel.add(centerPanel, BorderLayout.CENTER);
        add(panel);

        btnBuscar.addActionListener(e -> buscarPersona());
    }

    private JPanel labeledPanel(String label, Component comp) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void buscarPersona() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String[] datos = dao.obtenerPersonaBasicaPorId(id);

            if (datos == null) {
                JOptionPane.showMessageDialog(this, "No se encontró persona con ese ID");
                txtNombre.setText("");
                txtTelefonos.setText("");
                txtDirecciones.setText("");
                return;
            }

            txtNombre.setText(datos[0]);

            // Cargar teléfonos
            List<String> telefonos = dao.obtenerTelefonosPorPersona(id);
            txtTelefonos.setText(String.join("\n", telefonos));

            // Cargar direcciones
            List<String> direcciones = dao.obtenerDireccionesPorPersona(id);
            txtDirecciones.setText(String.join("\n", direcciones));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID válido");
        }
    }

    //public static void main(String[] args) {
       // SwingUtilities.invokeLater(() -> new ConsultaPersonaFrame().setVisible(true));
    //}
}