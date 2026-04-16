package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.auditoriaDAO;
import com.proyectofinallenguajes.model.auditoria;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmAuditoria extends JFrame {

    private JTable tabla;
    private JButton btnCargar;

    private final auditoriaDAO dao = new auditoriaDAO();

    public FrmAuditoria() {

        setTitle("Auditoría del Sistema");
        setSize(1100, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 80, 1040, 350);

        btnCargar = new JButton("Cargar Auditoría");
        btnCargar.setBounds(20, 20, 200, 30);

        add(scroll);
        add(btnCargar);

        btnCargar.addActionListener(e -> cargar());

        cargar();
    }

    private void cargar() {
        try {
            List<auditoria> lista = dao.listarAuditoria();

            String[] columnas = {
                "ID", "Tabla", "Operación", "Valor Anterior",
                "Valor Nuevo", "Fecha"
            };

            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (auditoria a : lista) {
                modelo.addRow(new Object[]{
                    a.getId_auditoria(),
                    a.getTabla_afectada(),
                    a.getOperacion(),
                    a.getValor_anterior(),
                    a.getValor_nuevo(),
                    a.getFecha()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar auditoría:\n" + e.getMessage());
        }
    }
}
