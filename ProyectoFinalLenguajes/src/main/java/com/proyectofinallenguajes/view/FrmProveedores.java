package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.proveedorDAO;
import com.proyectofinallenguajes.model.proveedor;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmProveedores extends JFrame {

    private JTable tabla;

    private JTextField txtIdProveedor;
    private JTextField txtNombre;
    private JTextField txtContacto;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final proveedorDAO dao = new proveedorDAO();

    public FrmProveedores() {
        setTitle("CRUD Proveedores");
        setSize(1050, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

      
        JLabel lblId = new JLabel("ID:");
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblContacto = new JLabel("Contacto:");
        JLabel lblTelefono = new JLabel("Teléfono:");
        JLabel lblCorreo = new JLabel("Correo:");

        lblId.setBounds(20, 0, 100, 20);
        lblNombre.setBounds(140, 0, 170, 20);
        lblContacto.setBounds(330, 0, 170, 20);
        lblTelefono.setBounds(520, 0, 140, 20);
        lblCorreo.setBounds(680, 0, 220, 20);

        txtIdProveedor = new JTextField();
        txtNombre = new JTextField();
        txtContacto = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        txtIdProveedor.setBounds(20, 20, 100, 25);
        txtNombre.setBounds(140, 20, 170, 25);
        txtContacto.setBounds(330, 20, 170, 25);
        txtTelefono.setBounds(520, 20, 140, 25);
        txtCorreo.setBounds(680, 20, 220, 25);

        txtIdProveedor.setEditable(false);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 990, 230);

        
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

        add(lblId);
        add(lblNombre);
        add(lblContacto);
        add(lblTelefono);
        add(lblCorreo);

        add(txtIdProveedor);
        add(txtNombre);
        add(txtContacto);
        add(txtTelefono);
        add(txtCorreo);

        add(scroll);

        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());
        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtIdProveedor.setText(tabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    txtContacto.setText(tabla.getValueAt(fila, 2).toString());
                    txtTelefono.setText(tabla.getValueAt(fila, 3).toString());
                    txtCorreo.setText(tabla.getValueAt(fila, 4).toString());
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<proveedor> lista = dao.listarProveedores();

            String[] columnas = {"ID", "Nombre", "Contacto", "Teléfono", "Correo", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (proveedor obj : lista) {
                modelo.addRow(new Object[]{
                    obj.getId_proveedor(),
                    obj.getNombre(),
                    obj.getContacto(),
                    obj.getTelefono(),
                    obj.getCorreo(),
                    obj.getEstado()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar proveedores:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertar() {
        if (txtNombre.getText().trim().isEmpty()
                || txtContacto.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        try {
            int idNuevo = dao.insertarProveedor(
                    txtNombre.getText().trim(),
                    txtContacto.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim(),
                    1
            );

            JOptionPane.showMessageDialog(this,
                    "Proveedor insertado correctamente. ID generado: " + idNuevo);

            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar proveedor:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (txtIdProveedor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.");
            return;
        }

        try {
            dao.actualizarProveedor(
                    Integer.parseInt(txtIdProveedor.getText().trim()),
                    txtNombre.getText().trim(),
                    txtContacto.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.");
            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar proveedor:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (txtIdProveedor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar lógicamente este proveedor?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.eliminarProveedor(Integer.parseInt(txtIdProveedor.getText().trim()));

            JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente.");
            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar proveedor:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdProveedor.setText("");
        txtNombre.setText("");
        txtContacto.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        tabla.clearSelection();
    }
}