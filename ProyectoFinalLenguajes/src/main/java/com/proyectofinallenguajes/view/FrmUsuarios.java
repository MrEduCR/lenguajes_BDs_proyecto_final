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
    private JTextField txtCorreo;
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

        // 🔹 LABELS
        JLabel lblId = new JLabel("ID");
        JLabel lblNombre = new JLabel("Nombre");
        JLabel lblCorreo = new JLabel("Correo");
        JLabel lblContrasena = new JLabel("Contraseña");
        JLabel lblRol = new JLabel("ID Rol");
        JLabel lblEstado = new JLabel("ID Estado");

        lblId.setBounds(20, 0, 80, 20);
        lblNombre.setBounds(120, 0, 150, 20);
        lblCorreo.setBounds(290, 0, 150, 20);
        lblContrasena.setBounds(460, 0, 150, 20);
        lblRol.setBounds(630, 0, 100, 20);
        lblEstado.setBounds(750, 0, 100, 20);

        // 🔹 CAMPOS
        txtId = new JTextField();
        txtNombre = new JTextField();
        txtCorreo = new JTextField();
        txtContrasena = new JTextField();
        txtIdRol = new JTextField();
        txtIdEstado = new JTextField();

        txtId.setBounds(20, 20, 80, 25);
        txtNombre.setBounds(120, 20, 150, 25);
        txtCorreo.setBounds(290, 20, 150, 25);
        txtContrasena.setBounds(460, 20, 150, 25);
        txtIdRol.setBounds(630, 20, 100, 25);
        txtIdEstado.setBounds(750, 20, 100, 25);

        txtId.setEditable(false);

        // 🔹 TABLA
        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 940, 230);

        // 🔹 BOTONES
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

        // 🔹 ADD
        add(lblId);
        add(lblNombre);
        add(lblCorreo);
        add(lblContrasena);
        add(lblRol);
        add(lblEstado);

        add(txtId);
        add(txtNombre);
        add(txtCorreo);
        add(txtContrasena);
        add(txtIdRol);
        add(txtIdEstado);

        add(scroll);

        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        // 🔹 EVENTOS
        btnCargar.addActionListener(e -> cargar());
        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();

                if (fila != -1) {
                    try {
                        int idUsuario = Integer.parseInt(
                                tabla.getValueAt(fila, 0).toString()
                        );

                        usuario u = dao.obtenerUsuario(idUsuario);

                        if (u != null) {
                            txtId.setText(String.valueOf(u.getId_usuario()));
                            txtNombre.setText(u.getNombre());
                            txtCorreo.setText(u.getCorreo());
                            txtIdRol.setText(String.valueOf(u.getId_rol()));
                            txtIdEstado.setText(String.valueOf(u.getId_estado()));
                            txtContrasena.setText("");
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error:\n" + ex.getMessage());
                    }
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<usuario> lista = dao.listarUsuarios();

            String[] columnas = {"ID", "Nombre", "Rol", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

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
            JOptionPane.showMessageDialog(this,
                    "Error:\n" + e.getMessage());
        }
    }

    private void insertar() {
        if (txtNombre.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty()
                || txtContrasena.getText().trim().isEmpty()
                || txtIdRol.getText().trim().isEmpty()
                || txtIdEstado.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        try {
            dao.insertarUsuario(
                    txtNombre.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtContrasena.getText().trim(),
                    Integer.parseInt(txtIdRol.getText().trim()),
                    Integer.parseInt(txtIdEstado.getText().trim())
            );

            JOptionPane.showMessageDialog(this, "Insertado");
            cargar();
            limpiar();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Rol y Estado deben ser números.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error:\n" + e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        try {
            dao.actualizarUsuario(
                    Integer.parseInt(txtId.getText().trim()),
                    txtNombre.getText().trim(),
                    txtCorreo.getText().trim(),
                    Integer.parseInt(txtIdRol.getText().trim())
            );

            JOptionPane.showMessageDialog(this, "Actualizado");
            cargar();
            limpiar();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Rol debe ser número.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error:\n" + e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar usuario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.eliminarUsuario(
                    Integer.parseInt(txtId.getText().trim())
            );

            JOptionPane.showMessageDialog(this, "Eliminado");
            cargar();
            limpiar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error:\n" + e.getMessage());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        txtIdRol.setText("");
        txtIdEstado.setText("");
        tabla.clearSelection();
    }
}