/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BajaPersonaFrame extends JFrame {
    private JTextField txtId;
    private JButton btnEliminar;

    public BajaPersonaFrame() {
        setTitle("Baja de Persona");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("ID de la persona:"));
        txtId = new JTextField();
        panel.add(txtId);

        btnEliminar = new JButton("Eliminar");
        panel.add(new JLabel()); // espacio
        panel.add(btnEliminar);

        add(panel);

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPersona();
            }
        });
    }

    private void eliminarPersona() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            PersonaDAO dao = new PersonaDAO();
            boolean exito = dao.eliminarPersona(id);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Persona eliminada correctamente.");
                txtId.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró persona con ese ID.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID válido.");
        }
    }

    //public static void main(String[] args) {
        //SwingUtilities.invokeLater(() -> new BajaPersonaFrame().setVisible(true));
    //}
}
