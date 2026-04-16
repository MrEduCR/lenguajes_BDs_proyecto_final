package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.orden;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class ordenDAO {

    public List<orden> listarOrdenes() throws SQLException {
        List<orden> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_ordenes(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Timestamp tsFecha = rs.getTimestamp("fecha");

                    orden obj = new orden(
                            rs.getInt("id_orden"),
                            tsFecha != null ? tsFecha.toLocalDateTime() : null,
                            rs.getString("cliente"),
                            rs.getString("usuario"),
                            rs.getString("estado"),
                            rs.getBigDecimal("total")
                    );

                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int crearOrden(int idCliente, int idUsuario, int idEstado) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_crear_orden(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idCliente);
            cs.setInt(2, idUsuario);
            cs.setInt(3, idEstado);
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();
            return cs.getInt(4);
        }
    }

    public void finalizarOrden(int idOrden) throws SQLException {
        String sql = "{ call pkg_ordenes.sp_finalizar_orden(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idOrden);
            cs.execute();
        }
    }

    public void cancelarOrden(int idOrden) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_cancelar_orden(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idOrden);
            cs.execute();
        }
    }

    public int totalOrdenesPendientes() throws SQLException {
        String sql = "{ ? = call pkg_ordenes.fn_total_ordenes_pendientes() }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            return cs.getInt(1);
        }
    }

    public orden obtenerOrden(int idOrden) throws SQLException {

    String sql = "{ call pkg_ordenes.sp_obtener_orden(?,?) }";

    try (Connection cn = DatabaseConnection.getConnection();
         CallableStatement cs = cn.prepareCall(sql)) {

        cs.setInt(1, idOrden);
        cs.registerOutParameter(2, OracleTypes.CURSOR);
        cs.execute();

        try (ResultSet rs = (ResultSet) cs.getObject(2)) {
            if (rs.next()) {

                orden o = new orden();
                Timestamp tsFecha = rs.getTimestamp("fecha");
                o.setId_orden(rs.getInt("id_orden"));
                o.setFecha(tsFecha != null ? tsFecha.toLocalDateTime() : null);
                o.setId_cliente(rs.getInt("id_cliente"));
                o.setId_usuario(rs.getInt("id_usuario"));
                o.setId_estado(rs.getInt("id_estado"));
                o.setCliente(rs.getString("cliente"));
                o.setUsuario(rs.getString("usuario"));
                o.setEstado(rs.getString("estado"));

                return o;
            }
        }
    }

    return null;
}
}
