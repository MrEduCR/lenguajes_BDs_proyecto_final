package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class usuarioDAO {

    public List<usuario> listarUsuarios() throws SQLException {
        List<usuario> lista = new ArrayList<>();

        String sql = "{ call pkg_usuarios.sp_listar_usuarios(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    usuario obj = new usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("rol"),
                            rs.getString("estado"));
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int insertarUsuario(String nombre, String correo,
            String contrasena,
            int idRol, int idEstado) throws SQLException {

        String sql = "{ call pkg_usuarios.sp_insertar_usuario(?,?,?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setString(2, correo);
            cs.setString(3, contrasena);
            cs.setInt(4, idRol);
            cs.setInt(5, idEstado);
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();
            return cs.getInt(6);
        }
    }

    public void actualizarUsuario(int idUsuario,
            String nombre,
            String correo,
            int idRol) throws SQLException {

        String sql = "{ call pkg_usuarios.sp_actualizar_usuario(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, nombre);
            cs.setString(3, correo);
            cs.setInt(4, idRol);

            cs.execute();
        }
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {

        String sql = "{ call pkg_usuarios.sp_eliminar_usuario(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.execute();
        }
    }

    public usuario obtenerUsuario(int idUsuario) throws SQLException {

        String sql = "{ call pkg_usuarios.sp_obtener_usuario(?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    usuario u = new usuario();

                    u.setId_usuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCorreo(rs.getString("correo"));
                    u.setId_rol(rs.getInt("id_rol"));
                    u.setId_estado(rs.getInt("id_estado"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));

                    return u;
                }
            }
        }

        return null;
    }

    public usuario login(String correo, String contrasena) throws SQLException {

        String sql = "{ call pkg_usuarios.sp_login(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, correo);
            cs.setString(2, contrasena);
            cs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);

            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                if (rs.next()) {
                    usuario u = new usuario();
                    u.setId_usuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCorreo(rs.getString("correo"));
                    u.setId_rol(rs.getInt("id_rol"));
                    u.setId_estado(rs.getInt("id_estado"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                    return u;
                }
            }
        }

        return null;
    }
}