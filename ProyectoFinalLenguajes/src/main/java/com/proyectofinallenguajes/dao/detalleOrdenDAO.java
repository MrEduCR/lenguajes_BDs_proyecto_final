package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.detalleOrden;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class detalleOrdenDAO {

    public List<detalleOrden> listarDetalles() throws SQLException {
        List<detalleOrden> lista = new ArrayList<>();

        String sql = "SELECT id_detalle, id_orden, producto, cantidad, precio_unitario, subtotal " +
                     "FROM vw_detalle_ordenes ORDER BY id_orden, id_detalle";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                detalleOrden obj = new detalleOrden(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_orden"),
                        rs.getString("producto"),
                        rs.getBigDecimal("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("subtotal")
                );
                lista.add(obj);
            }
        }

        return lista;
    }

    public List<detalleOrden> listarDetallesPorOrden(int idOrden) throws SQLException {
        List<detalleOrden> lista = new ArrayList<>();

        String sql = "SELECT id_detalle, id_orden, producto, cantidad, precio_unitario, subtotal " +
                     "FROM vw_detalle_ordenes WHERE id_orden = ? ORDER BY id_detalle";

        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idOrden);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalleOrden obj = new detalleOrden(
                            rs.getInt("id_detalle"),
                            rs.getInt("id_orden"),
                            rs.getString("producto"),
                            rs.getBigDecimal("cantidad"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getBigDecimal("subtotal")
                    );
                    lista.add(obj);
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
}
