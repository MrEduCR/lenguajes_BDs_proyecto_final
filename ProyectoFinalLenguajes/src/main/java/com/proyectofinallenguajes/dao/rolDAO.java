package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class rolDAO {

    public List<rol> listarRoles() throws SQLException {
        List<rol> lista = new ArrayList<>();

        String sql = "{ call pkg_roles.sp_listar_roles(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    rol obj = new rol(
                            rs.getInt("id_rol"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("id_estado"));
                    obj.setEstado(rs.getString("estado"));
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int insertarRol(String nombre, String descripcion, int idEstado) throws SQLException {

        String sql = "{ call pkg_roles.sp_insertar_rol(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setString(2, descripcion);
            cs.setInt(3, idEstado);
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();
            return cs.getInt(4);
        }
    }

    public void actualizarRol(int idRol, String nombre, String descripcion) throws SQLException {

        String sql = "{ call pkg_roles.sp_actualizar_rol(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idRol);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.execute();
        }
    }

    public void eliminarRol(int idRol) throws SQLException {

        String sql = "{ call pkg_roles.sp_eliminar_rol(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idRol);
            cs.execute();
        }
    }
}
