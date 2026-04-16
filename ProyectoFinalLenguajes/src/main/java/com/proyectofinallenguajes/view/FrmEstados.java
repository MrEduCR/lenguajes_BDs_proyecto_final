package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.estadoDAO;
import com.proyectofinallenguajes.model.estado;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmEstados extends JFrame {

    private JTable tabla;
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final estadoDAO dao = new estadoDAO();

    public FrmEstados() {
        setTitle("CRUD Estados");
        setSize(800, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 180, 740, 200);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();

        txtId.setBounds(20, 20, 80, 25);
        txtNombre.setBounds(120, 20, 150, 25);
        txtDescripcion.setBounds(290, 20, 200, 25);

        txtId.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 80, 100, 30);
        btnInsertar.setBounds(140, 80, 100, 30);
        btnActualizar.setBounds(260, 80, 120, 30);
        btnEliminar.setBounds(400, 80, 100, 30);
        btnLimpiar.setBounds(520, 80, 100, 30);

        add(scroll);
        add(txtId);
        add(txtNombre);
        add(txtDescripcion);
        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());
        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtId.setText(tabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    txtDescripcion.setText(tabla.getValueAt(fila, 2).toString());
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<estado> lista = dao.listarEstados();

            String[] columnas = {"ID", "Nombre", "Descripción"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (estado e : lista) {
                modelo.addRow(new Object[]{
                    e.getId_estado(),
                    e.getNombre(),
                    e.getDescripcion()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estados:\n" + e.getMessage());
        }
    }

    private void insertar() {
        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
            return;
        }

        try {
            dao.insertarEstado(txtNombre.getText(), txtDescripcion.getText());
            JOptionPane.showMessageDialog(this, "Estado insertado.");
            cargar();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al insertar:\n" + e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.");
            return;
        }

        try {
            dao.actualizarEstado(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText(),
                    txtDescripcion.getText()
            );

            JOptionPane.showMessageDialog(this, "Estado actualizado.");
            cargar();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar:\n" + e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.");
            return;
        }

        try {
            dao.eliminarEstado(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Estado eliminado.");
            cargar();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar:\n" + e.getMessage());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
