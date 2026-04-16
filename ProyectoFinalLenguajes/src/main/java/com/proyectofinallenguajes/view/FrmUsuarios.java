package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.usuarioDAO;
import com.proyectofinallenguajes.model.usuario;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmUsuarios extends JFrame {

    private JTable tabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtContrasena;
    private JTextField txtIdRol;
    private JTextField txtIdEstado;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final usuarioDAO dao = new usuarioDAO();

    public FrmUsuarios() {
        setTitle("CRUD Usuarios");
        setSize(1000, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 940, 230);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtContrasena = new JTextField();
        txtIdRol = new JTextField();
        txtIdEstado = new JTextField();

        txtId.setBounds(20, 20, 80, 25);
        txtNombre.setBounds(120, 20, 150, 25);
        txtContrasena.setBounds(290, 20, 150, 25);
        txtIdRol.setBounds(460, 20, 100, 25);
        txtIdEstado.setBounds(580, 20, 100, 25);

        txtId.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 100, 120, 30);
        btnInsertar.setBounds(160, 100, 120, 30);
        btnActualizar.setBounds(300, 100, 120, 30);
        btnEliminar.setBounds(440, 100, 120, 30);
        btnLimpiar.setBounds(580, 100, 120, 30);

        add(scroll);
        add(txtId);
        add(txtNombre);
        add(txtContrasena);
        add(txtIdRol);
        add(txtIdEstado);

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
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<usuario> lista = dao.listarUsuarios();

            String[] columnas = {"ID", "Nombre", "Rol", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (usuario u : lista) {
                modelo.addRow(new Object[]{
                    u.getId_usuario(),
                    u.getNombre(),
                    u.getRol(),
                    u.getEstado()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void insertar() {
        try {
            dao.insertarUsuario(
                    txtNombre.getText(),
                    txtContrasena.getText(),
                    Integer.parseInt(txtIdRol.getText()),
                    Integer.parseInt(txtIdEstado.getText())
            );

            JOptionPane.showMessageDialog(this, "Insertado");
            cargar();
            limpiar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void actualizar() {
        try {
            dao.actualizarUsuario(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText(),
                    Integer.parseInt(txtIdRol.getText()),
                    Integer.parseInt(txtIdEstado.getText())
            );

            JOptionPane.showMessageDialog(this, "Actualizado");
            cargar();
            limpiar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void eliminar() {
        try {
            dao.eliminarUsuario(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Eliminado");
            cargar();
            limpiar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtContrasena.setText("");
        txtIdRol.setText("");
        txtIdEstado.setText("");
    }
}
