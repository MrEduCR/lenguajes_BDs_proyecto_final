package com.proyectofinallenguajes.view;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FrmVistaReporte extends JFrame {

    private JTable tabla;

    public FrmVistaReporte(String titulo, DefaultTableModel modelo) {
        setTitle(titulo);
        setSize(1100, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        tabla = new JTable();
        tabla.setModel(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 20, 1040, 520);

        add(scroll);
    }
}