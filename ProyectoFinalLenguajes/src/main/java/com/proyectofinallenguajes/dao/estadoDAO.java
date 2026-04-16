package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.estado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class estadoDAO {

    public List<estado> listarEstados() throws SQLException {
        List<estado> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_estados(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    estado e = new estado(
                            rs.getInt("id_estado"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                    lista.add(e);
                }
            }
        }

        return lista;
    }

    public void insertarEstado(String nombre, String descripcion) throws SQLException {
    String sql = "{ call pkg_fabrica.sp_insertar_estado(?,?) }";
    
    try (Connection cn = DatabaseConnection.getConnection();
         CallableStatement cs = cn.prepareCall(sql)) {
        
        cs.setString(1, nombre);
        cs.setString(2, descripcion);
        cs.execute();
    }
}

    public void actualizarEstado(int id, String nombre, String descripcion) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_actualizar_estado(?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.execute();
        }
    }

    public void eliminarEstado(int id) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_eliminar_estado(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
        }
    }
}
