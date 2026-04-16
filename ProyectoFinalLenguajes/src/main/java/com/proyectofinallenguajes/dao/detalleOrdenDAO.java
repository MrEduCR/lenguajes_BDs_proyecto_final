package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.detalleOrden;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class detalleOrdenDAO {

    public List<detalleOrden> listarDetalles() throws SQLException {
        List<detalleOrden> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_detalles(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    lista.add(mapearDetalle(rs));
                }
            }
        }
        return lista;
    }

    public List<detalleOrden> listarDetallesPorOrden(int idOrden) throws SQLException {
        List<detalleOrden> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_detalles_por_orden(?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idOrden);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    lista.add(mapearDetalle(rs));
                }
            }
        }
        return lista;
    }

    public void insertarDetalle(int idOrden, int idItem, BigDecimal cantidad) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_agregar_detalle_orden(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idOrden);
            cs.setInt(2, idItem);
            cs.setBigDecimal(3, cantidad);
            cs.execute();
        }
    }

    public void actualizarDetalle(int idDetalle, BigDecimal cantidad) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_actualizar_detalle_orden(?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idDetalle);
            cs.setBigDecimal(2, cantidad);
            cs.execute();
        }
    }

    public void eliminarDetalle(int idDetalle) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_eliminar_detalle_orden(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idDetalle);
            cs.execute();
        }
    }

    private detalleOrden mapearDetalle(ResultSet rs) throws SQLException {
        return new detalleOrden(
                rs.getInt("id_detalle"),
                rs.getInt("id_orden"),
                rs.getString("producto"),
                rs.getBigDecimal("cantidad"),
                rs.getBigDecimal("precio_unitario"),
                rs.getBigDecimal("subtotal")
        );
    }
}