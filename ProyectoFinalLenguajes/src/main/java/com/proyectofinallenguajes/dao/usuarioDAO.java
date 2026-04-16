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

        String sql = "{ call pkg_fabrica.sp_listar_usuarios(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    usuario obj = new usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("rol"),
                            rs.getString("estado")
                    );
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int insertarUsuario(String nombre, String contrasena,
                               int idRol, int idEstado) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_insertar_usuario(?,?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setString(2, contrasena);
            cs.setInt(3, idRol);
            cs.setInt(4, idEstado);
            cs.registerOutParameter(5, Types.INTEGER);

            cs.execute();
            return cs.getInt(5);
        }
    }

    public void actualizarUsuario(int idUsuario, String nombre,
                                 int idRol, int idEstado) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_actualizar_usuario(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, nombre);
            cs.setInt(3, idRol);
            cs.setInt(4, idEstado);
            cs.execute();
        }
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_eliminar_usuario(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.execute();
        }
    }
}
