package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.rolDAO;
import com.proyectofinallenguajes.model.rol;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmRoles extends JFrame {

    private JTable tabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final rolDAO dao = new rolDAO();

    public FrmRoles() {
        setTitle("CRUD Roles");
        setSize(850, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 180, 780, 220);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();

        txtId.setBounds(20, 20, 80, 25);
        txtNombre.setBounds(120, 20, 180, 25);
        txtDescripcion.setBounds(320, 20, 200, 25);

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
            List<rol> lista = dao.listarRoles();

            String[] columnas = {"ID", "Nombre", "Descripción", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (rol r : lista) {
                modelo.addRow(new Object[]{
                    r.getId_rol(),
                    r.getNombre(),
                    r.getDescripcion(),
                    r.getEstado()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void insertar() {
        try {
            dao.insertarRol(txtNombre.getText(), txtDescripcion.getText(), 1);
            JOptionPane.showMessageDialog(this, "Insertado");
            cargar();
            limpiar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void actualizar() {
        try {
            dao.actualizarRol(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText(),
                    txtDescripcion.getText()
            );
            JOptionPane.showMessageDialog(this, "Actualizado");
            cargar();
            limpiar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void eliminar() {
        try {
            dao.eliminarRol(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Eliminado");
            cargar();
            limpiar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
