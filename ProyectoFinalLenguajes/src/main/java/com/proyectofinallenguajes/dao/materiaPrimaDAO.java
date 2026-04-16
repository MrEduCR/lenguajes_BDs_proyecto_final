package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.materiaPrima;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class materiaPrimaDAO {

    public List<materiaPrima> listarMateriasPrimas() throws SQLException {
        List<materiaPrima> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_materias_primas(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    materiaPrima obj = new materiaPrima(
                            rs.getInt("id_materia_prima"),
                            rs.getString("nombre_materia_prima"),
                            rs.getBigDecimal("precio_referencia"),
                            rs.getString("proveedor")
                    );
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int insertarMateriaPrima(int idProveedor, String nombre, BigDecimal precio) throws SQLException {
        String sql = "{ call pkg_materias_primas.sp_insertar_materia_prima(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idProveedor);
            cs.setString(2, nombre);
            cs.setBigDecimal(3, precio);
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();
            return cs.getInt(4);
        }
    }

    public void actualizarMateriaPrima(int idMateriaPrima, String nombre, BigDecimal precio) throws SQLException {
        String sql = "{ call pkg_materias_primas.sp_actualizar_materia_prima(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idMateriaPrima);
            cs.setString(2, nombre);
            cs.setBigDecimal(3, precio);
            cs.execute();
        }
    }

    public void eliminarMateriaPrima(int idMateriaPrima) throws SQLException {
        String sql = "{ call pkg_fabrica.sp_eliminar_materia_prima(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idMateriaPrima);
            cs.execute();
        }
    }

    public BigDecimal obtenerCostoPromedio() throws SQLException {
        String sql = "{ ? = call pkg_materias_primas.fn_costo_promedio_materias() }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.execute();
            return cs.getBigDecimal(1);
        }
    }
}
