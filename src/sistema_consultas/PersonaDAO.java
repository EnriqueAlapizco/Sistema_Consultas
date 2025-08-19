/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    // --- ALTA ---
    public boolean agregarPersona(String nombre, String direccion, List<String> telefonos) {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            if (con == null) throw new SQLException("No se pudo obtener conexión");

            con.setAutoCommit(false); // Inicia transacción

            // 1) Insert persona
            int personaId;
            String sqlPersona = "INSERT INTO persona (nombre, direccion) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, direccion);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        personaId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado de persona");
                    }
                }
            }

            // 2) Insertar telefonos si hay
            if (telefonos != null && !telefonos.isEmpty()) {
                String sqlTel = "INSERT INTO telefono (persona_id, numero) VALUES (?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sqlTel)) {
                    for (String tel : telefonos) {
                        if (tel == null) continue;
                        String t = tel.trim();
                        if (t.isEmpty()) continue;
                        pst.setInt(1, personaId);
                        pst.setString(2, t);
                        pst.addBatch();
                    }
                    pst.executeBatch();
                }
            }

            con.commit();
            return true;
        } catch (SQLException ex) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignore) {}
            }
            ex.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignore) {}
            }
        }
    }

    // --- CONSULTA: obtener todas las personas con sus teléfonos ---
    public List<String> obtenerTodasPersonas() {
        List<String> resultado = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.direccion, t.numero " +
                     "FROM persona p LEFT JOIN telefono t ON p.id = t.persona_id " +
                     "ORDER BY p.id";

        try (Connection con = Conexion.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int ultimoId = -1;
            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                int id = rs.getInt("id");
                if (id != ultimoId) {
                    // si ya había datos, agregar el anterior
                    if (ultimoId != -1) {
                        resultado.add(sb.toString());
                    }
                    // nueva persona
                    sb = new StringBuilder();
                    sb.append("ID: ").append(id)
                      .append(" | Nombre: ").append(rs.getString("nombre"))
                      .append(" | Dirección: ").append(rs.getString("direccion"))
                      .append(" | Teléfonos: ");
                    ultimoId = id;
                }
                String tel = rs.getString("numero");
                if (tel != null) {
                    sb.append(tel).append(", ");
                }
            }
            // agregar el último
            if (ultimoId != -1) {
                resultado.add(sb.toString());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultado;
    }

    // --- CONSULTA: buscar por nombre ---
    public List<String> buscarPorNombre(String nombre) {
        List<String> resultado = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.direccion, t.numero " +
                     "FROM persona p LEFT JOIN telefono t ON p.id = t.persona_id " +
                     "WHERE p.nombre LIKE ? ORDER BY p.id";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();

            int ultimoId = -1;
            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                int id = rs.getInt("id");
                if (id != ultimoId) {
                    if (ultimoId != -1) {
                        resultado.add(sb.toString());
                    }
                    sb = new StringBuilder();
                    sb.append("ID: ").append(id)
                      .append(" | Nombre: ").append(rs.getString("nombre"))
                      .append(" | Dirección: ").append(rs.getString("direccion"))
                      .append(" | Teléfonos: ");
                    ultimoId = id;
                }
                String tel = rs.getString("numero");
                if (tel != null) {
                    sb.append(tel).append(", ");
                }
            }
            if (ultimoId != -1) {
                resultado.add(sb.toString());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultado;
    }
    
    
    // ---  BAJAS --- 
    
    public boolean eliminarPersona(int id) {
    String sql = "DELETE FROM persona WHERE id = ?";
    try (Connection conn = Conexion.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        int filas = ps.executeUpdate();
        return filas > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    // ------ PARTE DE PRUEBA ------
    
    
    // --- CONSULTA: datos básicos por ID ---
public String[] obtenerPersonaBasicaPorId(int id) {
    String sql = "SELECT nombre, direccion FROM persona WHERE id = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new String[]{ rs.getString("nombre"), rs.getString("direccion") };
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return null; // no encontrada
}

// --- CONSULTA: teléfonos de una persona ---
public List<String> obtenerTelefonosPorPersona(int personaId) {
    List<String> telefonos = new ArrayList<>();
    String sql = "SELECT numero FROM telefono WHERE persona_id = ? ORDER BY id";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, personaId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                telefonos.add(rs.getString("numero"));
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return telefonos;
}

// --- MODIFICACIÓN (transacción): actualiza persona y reemplaza teléfonos ---
public boolean actualizarPersonaYTelefonos(int id, String nombre, String direccion, List<String> telefonos) {
    Connection con = null;
    try {
        con = Conexion.getConnection();
        if (con == null) throw new SQLException("No se pudo obtener conexión");
        con.setAutoCommit(false);

        // 1) Update persona
        String sqlPersona = "UPDATE persona SET nombre = ?, direccion = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, id);
            int filas = ps.executeUpdate();
            if (filas == 0) { // no existe ese id
                con.rollback();
                return false;
            }
        }

        // 2) Borrar teléfonos actuales
        String sqlDelTel = "DELETE FROM telefono WHERE persona_id = ?";
        try (PreparedStatement psDel = con.prepareStatement(sqlDelTel)) {
            psDel.setInt(1, id);
            psDel.executeUpdate();
        }

        // 3) Insertar teléfonos nuevos (si hay)
        if (telefonos != null && !telefonos.isEmpty()) {
            String sqlInsTel = "INSERT INTO telefono (persona_id, numero) VALUES (?, ?)";
            try (PreparedStatement pst = con.prepareStatement(sqlInsTel)) {
                for (String tel : telefonos) {
                    if (tel == null) continue;
                    String t = tel.trim();
                    if (t.isEmpty()) continue;
                    pst.setInt(1, id);
                    pst.setString(2, t);
                    pst.addBatch();
                }
                pst.executeBatch();
            }
        }

        con.commit();
        return true;

    } catch (SQLException ex) {
        if (con != null) {
            try { con.rollback(); } catch (SQLException ignore) {}
        }
        ex.printStackTrace();
        return false;
    } finally {
        if (con != null) {
            try { con.setAutoCommit(true); con.close(); } catch (SQLException ignore) {}
        }
    }
}
    
    
    
    
    
}
    
    
    
    

