package com.proyectofinallenguajes.dao;

import com.proyectofinallenguajes.conexion.DatabaseConnection;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import oracle.jdbc.OracleTypes;

public class viewDAO {

    public DefaultTableModel ejecutarVista(String procedimiento) throws SQLException {

        String sql = "{ call pkg_vistas." + procedimiento + "(?) }";

        try (Connection cn = DatabaseConnection.getConnection();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {

                ResultSetMetaData meta = rs.getMetaData();
                int columnas = meta.getColumnCount();

                String[] nombresColumnas = new String[columnas];
                for (int i = 1; i <= columnas; i++) {
                    nombresColumnas[i - 1] = meta.getColumnLabel(i);
                }

                DefaultTableModel modelo = new DefaultTableModel(nombresColumnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 1; i <= columnas; i++) {
                        fila[i - 1] = rs.getObject(i);
                    }
                    modelo.addRow(fila);
                }

                return modelo;
            }
        }
    }
}