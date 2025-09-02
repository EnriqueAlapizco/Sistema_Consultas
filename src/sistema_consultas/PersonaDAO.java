/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema_consultas;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PersonaDAO {

    // --- ALTA: Persona con varias direcciones y teléfonos ---
    public boolean agregarPersona(String nombre, List<String> direcciones, List<String> telefonos) {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            if (con == null) throw new SQLException("No se pudo obtener conexión");
            con.setAutoCommit(false);

            // 1) Insert persona
            int personaId;
            String sqlPersona = "INSERT INTO persona (nombre) VALUES (?)";
            try (PreparedStatement ps = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        personaId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado de persona");
                    }
                }
            }

            // 2) Insertar direcciones (si no existen, se crean) y relacionar
            if (direcciones != null && !direcciones.isEmpty()) {
                String sqlBuscarDir = "SELECT id FROM direccion WHERE calle = ?";
                String sqlInsertDir = "INSERT INTO direccion (calle) VALUES (?)";
                String sqlRel = "INSERT INTO persona_direccion (persona_id, direccion_id) VALUES (?, ?)";
                try (PreparedStatement psBuscar = con.prepareStatement(sqlBuscarDir);
                     PreparedStatement psInsert = con.prepareStatement(sqlInsertDir, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement psRel = con.prepareStatement(sqlRel)) {

                    for (String dir : direcciones) {
                        if (dir == null || dir.trim().isEmpty()) continue;
                        int dirId;

                        // Buscar si ya existe
                        psBuscar.setString(1, dir.trim());
                        try (ResultSet rs = psBuscar.executeQuery()) {
                            if (rs.next()) {
                                dirId = rs.getInt("id");
                            } else {
                                // Insertar nueva dirección
                                psInsert.setString(1, dir.trim());
                                psInsert.executeUpdate();
                                try (ResultSet rs2 = psInsert.getGeneratedKeys()) {
                                    if (rs2.next()) {
                                        dirId = rs2.getInt(1);
                                    } else {
                                        throw new SQLException("No se pudo obtener el ID de dirección");
                                    }
                                }
                            }
                        }

                        // Insertar relación persona_direccion
                        psRel.setInt(1, personaId);
                        psRel.setInt(2, dirId);
                        psRel.addBatch();
                    }
                    psRel.executeBatch();
                }
            }

            // 3) Insertar teléfonos
            if (telefonos != null && !telefonos.isEmpty()) {
                String sqlTel = "INSERT INTO telefono (persona_id, numero) VALUES (?, ?)";
                try (PreparedStatement psTel = con.prepareStatement(sqlTel)) {
                    for (String tel : telefonos) {
                        if (tel == null || tel.trim().isEmpty()) continue;
                        psTel.setInt(1, personaId);
                        psTel.setString(2, tel.trim());
                        psTel.addBatch();
                    }
                    psTel.executeBatch();
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
    
    
    
    // --- BAJAS ---
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
    
// --- CONSULTAS ---
    public String[] obtenerPersonaBasicaPorId(int id) {
        String sql = "SELECT nombre FROM persona WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{ rs.getString("nombre") };
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

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
        } catch (SQLException ex) { ex.printStackTrace(); }
        return telefonos;
    }

    
    
    public List<String> obtenerDireccionesPorPersona(int personaId) {
    List<String> direcciones = new ArrayList<>();
    String sql = "SELECT d.calle " +
                 "FROM direccion d " +
                 "JOIN persona_direccion pd ON d.id = pd.direccion_id " +
                 "WHERE pd.persona_id = ? " +
                 "ORDER BY d.id";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, personaId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                direcciones.add(rs.getString("calle"));
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return direcciones;
}

    
    
    //-- MODIFICACIONES --
    
    public boolean actualizarPersona(int id, String nombre, List<String> telefonos, List<Integer> direccionIds, List<String> nuevasDirecciones) {
    Connection con = null;
    try {
        con = Conexion.getConnection();
        if (con == null) throw new SQLException("No se pudo obtener conexión");
        con.setAutoCommit(false);

        // 1) Actualizar nombre
        String sqlPersona = "UPDATE persona SET nombre = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {
            ps.setString(1, nombre);
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                con.rollback();
                return false;
            }
        }

        // 2) Borrar teléfonos actuales
        try (PreparedStatement psDel = con.prepareStatement("DELETE FROM telefono WHERE persona_id = ?")) {
            psDel.setInt(1, id);
            psDel.executeUpdate();
        }

        // 3) Insertar teléfonos nuevos
        if (telefonos != null && !telefonos.isEmpty()) {
            try (PreparedStatement pst = con.prepareStatement("INSERT INTO telefono (persona_id, numero) VALUES (?, ?)")) {
                for (String tel : telefonos) {
                    if (tel == null || tel.trim().isEmpty()) continue;
                    pst.setInt(1, id);
                    pst.setString(2, tel.trim());
                    pst.addBatch();
                }
                pst.executeBatch();
            }
        }

        // 4) Gestionar direcciones
        try (PreparedStatement psDelDir = con.prepareStatement("DELETE FROM persona_direccion WHERE persona_id = ?")) {
            psDelDir.setInt(1, id);
            psDelDir.executeUpdate();
        }

        // 4a) Insertar nuevas direcciones en tabla 'direccion'
        if (nuevasDirecciones != null && !nuevasDirecciones.isEmpty()) {
            String sqlInsDir = "INSERT INTO direccion (calle) VALUES (?)";
            try (PreparedStatement pstDir = con.prepareStatement(sqlInsDir, Statement.RETURN_GENERATED_KEYS)) {
                for (String calle : nuevasDirecciones) {
                    pstDir.setString(1, calle);
                    pstDir.executeUpdate();
                    try (ResultSet rs = pstDir.getGeneratedKeys()) {
                        if (rs.next()) {
                            // Reemplazar el null correspondiente en direccionIds con el nuevo ID
                            direccionIds.set(direccionIds.indexOf(null), rs.getInt(1));
                        }
                    }
                }
            }
        }

        // 4b) Insertar todas las direcciones en persona_direccion
        if (direccionIds != null && !direccionIds.isEmpty()) {
            String sqlPersDir = "INSERT INTO persona_direccion (persona_id, direccion_id) VALUES (?, ?)";
            try (PreparedStatement pstDir = con.prepareStatement(sqlPersDir)) {
                for (Integer dirId : direccionIds) {
                    if (dirId == null) continue; // ya insertadas
                    pstDir.setInt(1, id);
                    pstDir.setInt(2, dirId);
                    pstDir.addBatch();
                }
                pstDir.executeBatch();
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
