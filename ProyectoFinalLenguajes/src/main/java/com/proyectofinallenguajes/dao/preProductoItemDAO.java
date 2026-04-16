package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.preProductoItem;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class preProductoItemDAO {

    public List<preProductoItem> listarReceta(int idItem) throws SQLException {
        List<preProductoItem> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_receta(?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idItem);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    preProductoItem obj = new preProductoItem(
                            rs.getInt("id_pre_producto"),
                            rs.getString("producto"),
                            rs.getString("nombre_materia_prima"),
                            rs.getBigDecimal("medida_materia_prima"),
                            rs.getString("unidad_medida"),
                            rs.getBigDecimal("precio_referencia")
                    );
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public void insertarReceta(int idItem, int idMateriaPrima, BigDecimal medida, String unidadMedida)
            throws SQLException {

        String sql = "{ call pkg_items.sp_insertar_receta(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idItem);
            cs.setInt(2, idMateriaPrima);
            cs.setBigDecimal(3, medida);
            cs.setString(4, unidadMedida);
            cs.execute();
        }
    }

    public void eliminarReceta(int idPreProducto) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_eliminar_receta(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idPreProducto);
            cs.execute();
        }
    }
}
