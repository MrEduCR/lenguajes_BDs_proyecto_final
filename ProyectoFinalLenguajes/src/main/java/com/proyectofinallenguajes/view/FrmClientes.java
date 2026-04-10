package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.clienteDAO;
import javax.swing.*;

public class FrmClientes extends JFrame {

    private JTable tabla;
    private JTextField txtNombre, txtId, txtTelefono, txtCorreo, txtIdentificacion;
    private JButton btnCargar, btnInsertar, btnActualizar, btnEliminar;

    private clienteDAO dao = new clienteDAO();

    public FrmClientes() {

        setTitle("CRUD Clientes");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 200, 850, 200);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtIdentificacion = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        txtId.setBounds(20, 20, 100, 25);
        txtNombre.setBounds(140, 20, 150, 25);
        txtIdentificacion.setBounds(310, 20, 150, 25);
        txtTelefono.setBounds(480, 20, 150, 25);
        txtCorreo.setBounds(650, 20, 200, 25);

        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        btnCargar.setBounds(20, 100, 120, 30);
        btnInsertar.setBounds(160, 100, 120, 30);
        btnActualizar.setBounds(300, 100, 120, 30);
        btnEliminar.setBounds(440, 100, 120, 30);

        add(scroll);
        add(txtId);
        add(txtNombre);
        add(txtIdentificacion);
        add(txtTelefono);
        add(txtCorreo);
        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);

        // EVENTOS

        btnCargar.addActionListener(e -> cargar());

        btnInsertar.addActionListener(e -> {
            dao.insertarCliente(
                    txtNombre.getText(),
                    txtIdentificacion.getText(),
                    txtTelefono.getText(),
                    txtCorreo.getText(),
                    1 // estado activo
            );
            cargar();
        });

        btnActualizar.addActionListener(e -> {
            dao.actualizarCliente(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText(),
                    txtTelefono.getText(),
                    txtCorreo.getText()
            );
            cargar();
        });

        btnEliminar.addActionListener(e -> {
            dao.eliminarCliente(
                    Integer.parseInt(txtId.getText())
            );
            cargar();
        });

        // CLICK EN TABLA → llenar inputs

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtId.setText(tabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    txtIdentificacion.setText(tabla.getValueAt(fila, 2).toString());
                    txtTelefono.setText(tabla.getValueAt(fila, 3).toString());
                    txtCorreo.setText(tabla.getValueAt(fila, 4).toString());
                }
            }
        });
    }

    private void cargar() {
        tabla.setModel(new clienteDAO().listarClientes());
    }
}