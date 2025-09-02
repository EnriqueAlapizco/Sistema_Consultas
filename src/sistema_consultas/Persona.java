/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;
import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private List<Integer> direccionIds; // IDs de las direcciones asociadas
    private List<String> telefonos;

    public Persona() {}

    public Persona(int id, String nombre, List<Integer> direccionIds, List<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.direccionIds = direccionIds;
        this.telefonos = telefonos;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Integer> getDireccionIds() { return direccionIds; }
    public void setDireccionIds(List<Integer> direccionIds) { this.direccionIds = direccionIds; }
    public List<String> getTelefonos() { return telefonos; }
    public void setTelefonos(List<String> telefonos) { this.telefonos = telefonos; }
}