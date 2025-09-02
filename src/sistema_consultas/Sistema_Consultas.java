/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistema_consultas;


import javax.swing.*;
import java.awt.*;

public class Sistema_Consultas extends JFrame {

    public Sistema_Consultas() {
        setTitle("Sistema de Consultas - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        // Botones para cada operación
        JButton btnAlta = new JButton("Alta de Persona");
        JButton btnBaja = new JButton("Baja de Persona");
        JButton btnModificar = new JButton("Modificar Persona");
        JButton btnConsultar = new JButton("Consultar Persona");

        // Agregar botones al frame
        add(btnAlta);
        add(btnBaja);
        add(btnModificar);
        add(btnConsultar);

        // Listeners para abrir cada ventana
        btnAlta.addActionListener(e -> SwingUtilities.invokeLater(() -> new AltaPersonaFrame().setVisible(true)));
        btnBaja.addActionListener(e -> SwingUtilities.invokeLater(() -> new BajaPersonaFrame().setVisible(true)));
        btnModificar.addActionListener(e -> SwingUtilities.invokeLater(() -> new ModificarPersonaFrame().setVisible(true)));
        btnConsultar.addActionListener(e -> SwingUtilities.invokeLater(() -> new ConsultaPersonaFrame().setVisible(true)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Sistema_Consultas sistema = new Sistema_Consultas();
            sistema.setVisible(true);
        });
    }
}