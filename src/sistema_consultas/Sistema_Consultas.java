/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistema_consultas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Sistema_Consultas extends JFrame {

    public Sistema_Consultas() {
        setTitle("Sistema de Consultas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton btnAlta = new JButton("Alta de Persona");
        JButton btnBaja = new JButton("Baja de Persona");
        JButton btnConsulta = new JButton("Consultar Personas");
        JButton btnModificacion = new JButton("Modificar Persona");

        add(btnAlta);
        add(btnBaja);
        add(btnConsulta);
        add(btnModificacion);

        // Listeners para abrir cada ventana
        btnAlta.addActionListener((ActionEvent e) -> new AltaPersonaFrame().setVisible(true));
        btnBaja.addActionListener((ActionEvent e) -> new BajaPersonaFrame().setVisible(true));
        btnConsulta.addActionListener((ActionEvent e) -> new ConsultaPersonaFrame().setVisible(true));
        btnModificacion.addActionListener((ActionEvent e) -> new ModificarPersonaFrame().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new Sistema_Consultas().setVisible(true);
        });
    }
}
