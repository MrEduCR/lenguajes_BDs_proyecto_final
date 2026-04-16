package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.auditoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class auditoriaDAO {

    public List<auditoria> listarAuditoria() throws SQLException {

        List<auditoria> lista = new ArrayList<>();

        String sql = "SELECT id_auditoria, tabla_afectada, operacion, " +
                     "valor_anterior, valor_nuevo, fecha " +
                     "FROM auditoria ORDER BY fecha DESC";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp ts = rs.getTimestamp("fecha");

                auditoria obj = new auditoria(
                        rs.getInt("id_auditoria"),
                        rs.getString("tabla_afectada"),
                        rs.getString("operacion"),
                        rs.getString("valor_anterior"),
                        rs.getString("valor_nuevo"),
                        ts != null ? ts.toLocalDateTime() : null
                );

                lista.add(obj);
            }
        }

        return lista;
    }
}
