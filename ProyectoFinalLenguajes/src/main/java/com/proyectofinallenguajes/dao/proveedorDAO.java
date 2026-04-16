package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import com.proyectofinallenguajes.model.proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class proveedorDAO {

    public List<proveedor> listarProveedores() throws SQLException {
        List<proveedor> lista = new ArrayList<>();

        String sql = "{ call pkg_fabrica.sp_listar_proveedores(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    proveedor obj = new proveedor(
                            rs.getInt("id_proveedor"),
                            rs.getString("nombre"),
                            rs.getString("contacto"),
                            rs.getString("telefono"),
                            rs.getString("correo"),
                            rs.getString("estado")
                    );
                    lista.add(obj);
                }
            }
        }

        return lista;
    }

    public int insertarProveedor(String nombre, String contacto, String telefono,
                                 String correo, int idEstado) throws SQLException {

        String sql = "{ call pkg_fabrica.sp_insertar_proveedor(?,?,?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setString(2, contacto);
            cs.setString(3, telefono);
            cs.setString(4, correo);
            cs.setInt(5, idEstado);
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();
            return cs.getInt(6);
        }
    }

    public void actualizarProveedor(int idProveedor, String nombre, String contacto,
                                    String telefono, String correo) throws SQLException {

        String sql = "{ call pkg_proveedores.sp_actualizar_proveedor(?,?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idProveedor);
            cs.setString(2, nombre);
            cs.setString(3, contacto);
            cs.setString(4, telefono);
            cs.setString(5, correo);
            cs.execute();
        }
    }

    public void eliminarProveedor(int idProveedor) throws SQLException {
        String sql = "{ call pkg_proveedores.sp_eliminar_proveedor(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idProveedor);
            cs.execute();
        }
    }
}
