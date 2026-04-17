package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.clienteDAO;
import javax.swing.*;

public class FrmClientes extends JFrame {

    private JTable tabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtIdentificacion;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private clienteDAO dao = new clienteDAO();

    public FrmClientes() {

        setTitle("Gestión de Clientes");
        setSize(950, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 220, 890, 250);

        JLabel lblId = new JLabel("ID Cliente:");
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblIdentificacion = new JLabel("Identificación:");
        JLabel lblTelefono = new JLabel("Teléfono:");
        JLabel lblCorreo = new JLabel("Correo:");

        lblId.setBounds(20, 20, 100, 20);
        lblNombre.setBounds(140, 20, 100, 20);
        lblIdentificacion.setBounds(310, 20, 100, 20);
        lblTelefono.setBounds(480, 20, 100, 20);
        lblCorreo.setBounds(650, 20, 100, 20);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtIdentificacion = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        txtId.setBounds(20, 45, 100, 25);
        txtNombre.setBounds(140, 45, 150, 25);
        txtIdentificacion.setBounds(310, 45, 150, 25);
        txtTelefono.setBounds(480, 45, 150, 25);
        txtCorreo.setBounds(650, 45, 200, 25);

        txtId.setEditable(false);

        JLabel lblDescripcion = new JLabel("Recuerde no divulgar el ID del cliente cuando sea generado. Es de uso confidencial");
        lblDescripcion.setBounds(20, 85, 700, 20);

        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 120, 120, 30);
        btnInsertar.setBounds(160, 120, 120, 30);
        btnActualizar.setBounds(300, 120, 120, 30);
        btnEliminar.setBounds(440, 120, 120, 30);
        btnLimpiar.setBounds(580, 120, 120, 30);

        add(scroll);

        add(lblId);
        add(lblNombre);
        add(lblIdentificacion);
        add(lblTelefono);
        add(lblCorreo);
        add(lblDescripcion);

        add(txtId);
        add(txtNombre);
        add(txtIdentificacion);
        add(txtTelefono);
        add(txtCorreo);

        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());

        btnInsertar.addActionListener(e -> {
            if (!validarCampos()) return;

            dao.insertarCliente(
                    txtNombre.getText().trim(),
                    txtIdentificacion.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    1
            );

            JOptionPane.showMessageDialog(this, "Cliente insertado correctamente.");
            cargar();
            limpiarCampos();
        });

        btnActualizar.addActionListener(e -> {

            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
                return;
            }

            if (!validarCampos()) return;

            dao.actualizarCliente(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            cargar();
            limpiarCampos();
        });

        btnEliminar.addActionListener(e -> {

            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
                return;
            }

            dao.eliminarCliente(Integer.parseInt(txtId.getText()));

            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
            cargar();
            limpiarCampos();
        });

        btnLimpiar.addActionListener(e -> limpiarCampos());

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

        cargar();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()
                || txtIdentificacion.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "No se permiten campos vacios",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return true;
    }

    private void cargar() {
        tabla.setModel(dao.listarClientes());
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtIdentificacion.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        tabla.clearSelection();
    }
}