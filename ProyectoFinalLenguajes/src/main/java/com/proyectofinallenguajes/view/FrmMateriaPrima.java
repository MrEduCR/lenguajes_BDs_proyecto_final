package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.materiaPrimaDAO;
import com.proyectofinallenguajes.model.materiaPrima;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmMateriaPrima extends JFrame {

    private JTable tabla;
    private JTextField txtIdMateriaPrima;
    private JTextField txtIdProveedor;
    private JTextField txtNombreMateriaPrima;
    private JTextField txtPrecioReferencia;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnPromedio;
    private JButton btnLimpiar;

    private final materiaPrimaDAO dao = new materiaPrimaDAO();

    public FrmMateriaPrima() {
        setTitle("CRUD Materia Prima");
        setSize(980, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 920, 230);

        txtIdMateriaPrima = new JTextField();
        txtIdProveedor = new JTextField();
        txtNombreMateriaPrima = new JTextField();
        txtPrecioReferencia = new JTextField();

        txtIdMateriaPrima.setBounds(20, 20, 100, 25);
        txtIdProveedor.setBounds(140, 20, 100, 25);
        txtNombreMateriaPrima.setBounds(260, 20, 220, 25);
        txtPrecioReferencia.setBounds(500, 20, 140, 25);

        txtIdMateriaPrima.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnPromedio = new JButton("Costo promedio");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 100, 120, 30);
        btnInsertar.setBounds(160, 100, 120, 30);
        btnActualizar.setBounds(300, 100, 120, 30);
        btnEliminar.setBounds(440, 100, 120, 30);
        btnPromedio.setBounds(580, 100, 140, 30);
        btnLimpiar.setBounds(740, 100, 120, 30);

        add(scroll);
        add(txtIdMateriaPrima);
        add(txtIdProveedor);
        add(txtNombreMateriaPrima);
        add(txtPrecioReferencia);

        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnPromedio);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());
        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnPromedio.addActionListener(e -> mostrarPromedio());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtIdMateriaPrima.setText(tabla.getValueAt(fila, 0).toString());
                    txtNombreMateriaPrima.setText(tabla.getValueAt(fila, 1).toString());
                    txtPrecioReferencia.setText(tabla.getValueAt(fila, 2).toString());
                    txtIdProveedor.setText("");
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<materiaPrima> lista = dao.listarMateriasPrimas();

            String[] columnas = {"ID", "Nombre", "Precio Referencia", "Proveedor"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (materiaPrima obj : lista) {
                modelo.addRow(new Object[]{
                    obj.getId_materia_prima(),
                    obj.getNombre_materia_prima(),
                    obj.getPrecio_referencia(),
                    obj.getProveedor()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar materias primas:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertar() {
        if (txtIdProveedor.getText().trim().isEmpty()
                || txtNombreMateriaPrima.getText().trim().isEmpty()
                || txtPrecioReferencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete proveedor, nombre y precio.");
            return;
        }

        try {
            int idProveedor = Integer.parseInt(txtIdProveedor.getText().trim());
            BigDecimal precio = new BigDecimal(txtPrecioReferencia.getText().trim());

            int idNuevo = dao.insertarMateriaPrima(
                    idProveedor,
                    txtNombreMateriaPrima.getText().trim(),
                    precio
            );

            JOptionPane.showMessageDialog(this,
                    "Materia prima insertada correctamente. ID generado: " + idNuevo);

            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Proveedor y precio deben ser numéricos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar materia prima:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (txtIdMateriaPrima.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia prima de la tabla.");
            return;
        }

        try {
            int idMateriaPrima = Integer.parseInt(txtIdMateriaPrima.getText().trim());
            BigDecimal precio = new BigDecimal(txtPrecioReferencia.getText().trim());

            dao.actualizarMateriaPrima(
                    idMateriaPrima,
                    txtNombreMateriaPrima.getText().trim(),
                    precio
            );

            JOptionPane.showMessageDialog(this, "Materia prima actualizada correctamente.");
            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser numérico.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar materia prima:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (txtIdMateriaPrima.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia prima de la tabla.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar esta materia prima?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int idMateriaPrima = Integer.parseInt(txtIdMateriaPrima.getText().trim());
            dao.eliminarMateriaPrima(idMateriaPrima);

            JOptionPane.showMessageDialog(this, "Materia prima eliminada correctamente.");
            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar materia prima:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarPromedio() {
        try {
            BigDecimal promedio = dao.obtenerCostoPromedio();
            JOptionPane.showMessageDialog(this,
                    "Costo promedio de materias primas: " + promedio);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener costo promedio:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdMateriaPrima.setText("");
        txtIdProveedor.setText("");
        txtNombreMateriaPrima.setText("");
        txtPrecioReferencia.setText("");
        tabla.clearSelection();
    }
}
