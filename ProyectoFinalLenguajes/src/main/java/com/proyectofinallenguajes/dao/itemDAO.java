package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.item;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class itemDAO {

    public List<item> listarItems() throws SQLException {
        List<item> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_items(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    item obj = new item(
                            rs.getInt("id_item"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getString("unidad_medida"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getString("estado")
                    );
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int insertarItem(String nombre, String descripcion, String unidadMedida,
                            BigDecimal precio, int idEstado) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_insertar_item(?,?,?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setString(2, descripcion);
            cs.setString(3, unidadMedida);
            cs.setBigDecimal(4, precio);
            cs.setInt(5, idEstado);
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();
            return cs.getInt(6);
        }
    }

    public void actualizarItem(int idItem, String nombre, BigDecimal precio) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_actualizar_item(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idItem);
            cs.setString(2, nombre);
            cs.setBigDecimal(3, precio);
            cs.execute();
        }
    }

    public void eliminarItem(int idItem) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_eliminar_item(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idItem);
            cs.execute();
        }
    }
}
