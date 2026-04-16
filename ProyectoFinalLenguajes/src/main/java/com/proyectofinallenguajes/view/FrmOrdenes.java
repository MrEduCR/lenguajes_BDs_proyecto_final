package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.ordenDAO;
import com.proyectofinallenguajes.model.orden;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmOrdenes extends JFrame {

    private JTable tabla;

    private JTextField txtIdOrden;
    private JTextField txtIdCliente;
    private JTextField txtIdUsuario;
    private JTextField txtIdEstado;

    private JButton btnCargar;
    private JButton btnCrear;
    private JButton btnFinalizar;
    private JButton btnCancelar;
    private JButton btnPendientes;
    private JButton btnLimpiar;

    private final ordenDAO dao = new ordenDAO();

    public FrmOrdenes() {
        setTitle("CRUD Ordenes");
        setSize(1050, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 990, 230);

        txtIdOrden = new JTextField();
        txtIdCliente = new JTextField();
        txtIdUsuario = new JTextField();
        txtIdEstado = new JTextField();

        txtIdOrden.setBounds(20, 20, 100, 25);
        txtIdCliente.setBounds(140, 20, 100, 25);
        txtIdUsuario.setBounds(260, 20, 100, 25);
        txtIdEstado.setBounds(380, 20, 100, 25);

        txtIdOrden.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnCrear = new JButton("Crear orden");
        btnFinalizar = new JButton("Finalizar");
        btnCancelar = new JButton("Cancelar");
        btnPendientes = new JButton("Pendientes");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 100, 120, 30);
        btnCrear.setBounds(160, 100, 140, 30);
        btnFinalizar.setBounds(320, 100, 120, 30);
        btnCancelar.setBounds(460, 100, 120, 30);
        btnPendientes.setBounds(600, 100, 120, 30);
        btnLimpiar.setBounds(740, 100, 120, 30);

        add(scroll);
        add(txtIdOrden);
        add(txtIdCliente);
        add(txtIdUsuario);
        add(txtIdEstado);

        add(btnCargar);
        add(btnCrear);
        add(btnFinalizar);
        add(btnCancelar);
        add(btnPendientes);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());
        btnCrear.addActionListener(e -> crearOrden());
        btnFinalizar.addActionListener(e -> finalizarOrden());
        btnCancelar.addActionListener(e -> cancelarOrden());
        btnPendientes.addActionListener(e -> mostrarPendientes());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();

                if (fila != -1) {
                    try {
                        int idOrden = Integer.parseInt(
                                tabla.getValueAt(fila, 0).toString());

                        orden o = dao.obtenerOrden(idOrden);

                        if (o != null) {
                            txtIdOrden.setText(String.valueOf(o.getId_orden()));
                            txtIdCliente.setText(String.valueOf(o.getId_cliente()));
                            txtIdUsuario.setText(String.valueOf(o.getId_usuario()));
                            txtIdEstado.setText(String.valueOf(o.getId_estado()));
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
            List<orden> lista = dao.listarOrdenes();

            String[] columnas = { "ID Orden", "Fecha", "Cliente", "Usuario", "Estado", "Total" };
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (orden obj : lista) {
                modelo.addRow(new Object[] {
                        obj.getId_orden(),
                        obj.getFecha(),
                        obj.getCliente(),
                        obj.getUsuario(),
                        obj.getEstado(),
                        obj.getTotal()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar órdenes:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearOrden() {
        if (txtIdCliente.getText().trim().isEmpty()
                || txtIdUsuario.getText().trim().isEmpty()
                || txtIdEstado.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete cliente, usuario y estado.");
            return;
        }

        try {
            int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            int idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());
            int idEstado = Integer.parseInt(txtIdEstado.getText().trim());

            int idNuevo = dao.crearOrden(idCliente, idUsuario, idEstado);

            JOptionPane.showMessageDialog(this,
                    "Orden creada correctamente. ID generado: " + idNuevo);

            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Cliente, usuario y estado deben ser numéricos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear orden:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarOrden() {
        if (txtIdOrden.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden.");
            return;
        }

        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            dao.finalizarOrden(idOrden);

            JOptionPane.showMessageDialog(this, "Orden finalizada correctamente.");
            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al finalizar orden:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarOrden() {
        if (txtIdOrden.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cancelar esta orden?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            dao.cancelarOrden(idOrden);

            JOptionPane.showMessageDialog(this, "Orden cancelada correctamente.");
            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cancelar orden:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarPendientes() {
        try {
            int total = dao.totalOrdenesPendientes();
            JOptionPane.showMessageDialog(this,
                    "Total de órdenes pendientes: " + total);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar órdenes pendientes:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdOrden.setText("");
        txtIdCliente.setText("");
        txtIdUsuario.setText("");
        txtIdEstado.setText("");
        tabla.clearSelection();
    }
}
