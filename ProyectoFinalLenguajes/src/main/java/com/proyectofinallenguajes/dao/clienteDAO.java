package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import java.sql.*;

import javax.swing.table.DefaultTableModel;

public class clienteDAO {

    public DefaultTableModel listarClientes() {

        String[] columnas = { "ID", "Nombre", "Identificación", "Teléfono", "Correo", "Estado" };
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "{ call pkg_fabrica.sp_listar_clientes(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("identificacion"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("estado")
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return modelo;
    }

    public void insertarCliente(String nombre, String identificacion, String telefono, String correo, int idEstado) {

        String sql = "{ call pkg_fabrica.sp_insertar_cliente(?,?,?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setString(1, nombre);
            cs.setString(2, identificacion);
            cs.setString(3, telefono);
            cs.setString(4, correo);
            cs.setInt(5, idEstado);

            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();

            int idNuevo = cs.getInt(6);
            System.out.println("Insertado ID: " + idNuevo);

        } catch (SQLException e) {
            System.out.println("Error al insertar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarCliente(int id, String nombre,
            String telefono, String correo) {

        String sql = "{ call pkg_fabrica.sp_actualizar_cliente(?,?,?,?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.setString(2, nombre);
            cs.setString(3, telefono);
            cs.setString(4, correo);

            cs.execute();

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarCliente(int id) {

        String sql = "{ call pkg_fabrica.sp_eliminar_cliente(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
                CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();

        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente: " + e.getMessage());
             e.printStackTrace();
        }
    }
}