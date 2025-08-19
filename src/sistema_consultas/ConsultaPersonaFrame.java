/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConsultaPersonaFrame extends JFrame {
    private PersonaDAO personaDAO;
    private JTable tabla;
    private DefaultTableModel modelo;

    public ConsultaPersonaFrame() {
        personaDAO = new PersonaDAO();
        setTitle("Consulta de Personas");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Modelo de tabla
        modelo = new DefaultTableModel(new String[]{"Registro"}, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botón para actualizar la tabla
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarPersonas());
        add(btnActualizar, BorderLayout.SOUTH);

        cargarPersonas(); // cargar al inicio
    }

    private void cargarPersonas() {
        modelo.setRowCount(0); // limpiar tabla
        List<String> lista = personaDAO.obtenerTodasPersonas();
        for (String registro : lista) {
            modelo.addRow(new Object[]{registro});
        }
    }
    
    
   // public static void main(String[] args) {
       // SwingUtilities.invokeLater(() -> {
          //  try {
                // look & feel del sistema
             //   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           // } catch (Exception ignored) {}

           // new ConsultaPersonaFrame().setVisible(true);
       // });
   // }
    
}