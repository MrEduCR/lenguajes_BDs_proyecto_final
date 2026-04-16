package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.inventarioDeItems;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class inventarioDeItemsDAO {

    public List<inventarioDeItems> listarInventario() throws SQLException {
        List<inventarioDeItems> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_inventario(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Timestamp tsIngreso = rs.getTimestamp("fecha_ingreso");
                    Timestamp tsVenc = rs.getTimestamp("fecha_vencimiento");

                    inventarioDeItems obj = new inventarioDeItems(
                            rs.getInt("id_lote"),
                            rs.getInt("id_item"),
                            rs.getString("producto"),
                            rs.getBigDecimal("cantidad"),
                            tsIngreso != null ? tsIngreso.toLocalDateTime() : null,
                            tsVenc != null ? tsVenc.toLocalDateTime() : null
                    );

                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public void ingresarLote(int idItem, BigDecimal cantidad, Timestamp fechaVencimiento) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_ingresar_lote(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idItem);
            cs.setBigDecimal(2, cantidad);

            if (fechaVencimiento != null) {
                cs.setTimestamp(3, fechaVencimiento);
            } else {
                cs.setNull(3, Types.TIMESTAMP);
            }

            cs.execute();
        }
    }

    public void ajustarInventario(int idLote, BigDecimal nuevaCantidad) throws SQLException {
        String sql = "{ call pkg_inventario.sp_ajustar_inventario(?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idLote);
            cs.setBigDecimal(2, nuevaCantidad);
            cs.execute();
        }
    }

    public void rebajarInventario(int idItem, BigDecimal cantidad) throws SQLException {
        String sql = "{ call pkg_inventario.sp_rebajar_inventario(?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idItem);
            cs.setBigDecimal(2, cantidad);
            cs.execute();
        }
    }

    public BigDecimal obtenerValorTotalInventario() throws SQLException {
        String sql = "{ ? = call pkg_inventario.fn_valor_total_inventario() }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.execute();

            return cs.getBigDecimal(1);
        }
    }
}
