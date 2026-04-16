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

        JLabel lblIdMateriaPrima = new JLabel("ID Materia Prima:");
        JLabel lblIdProveedor = new JLabel("ID Proveedor:");
        JLabel lblNombreMateriaPrima = new JLabel("Nombre:");
        JLabel lblPrecioReferencia = new JLabel("Precio Referencia:");

        lblIdMateriaPrima.setBounds(20, 20, 120, 20);
        lblIdProveedor.setBounds(160, 20, 100, 20);
        lblNombreMateriaPrima.setBounds(300, 20, 100, 20);
        lblPrecioReferencia.setBounds(560, 20, 140, 20);

   
        txtIdMateriaPrima = new JTextField();
        txtIdProveedor = new JTextField();
        txtNombreMateriaPrima = new JTextField();
        txtPrecioReferencia = new JTextField();

        txtIdMateriaPrima.setBounds(20, 45, 120, 25);
        txtIdProveedor.setBounds(160, 45, 120, 25);
        txtNombreMateriaPrima.setBounds(300, 45, 220, 25);
        txtPrecioReferencia.setBounds(560, 45, 140, 25);

        txtIdMateriaPrima.setEditable(false);


        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnPromedio = new JButton("Costo promedio");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 110, 120, 30);
        btnInsertar.setBounds(160, 110, 120, 30);
        btnActualizar.setBounds(300, 110, 120, 30);
        btnEliminar.setBounds(440, 110, 120, 30);
        btnPromedio.setBounds(580, 110, 140, 30);
        btnLimpiar.setBounds(740, 110, 120, 30);

   
        add(scroll);

        add(lblIdMateriaPrima);
        add(lblIdProveedor);
        add(lblNombreMateriaPrima);
        add(lblPrecioReferencia);

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

            String[] columnas = {
                    "ID",
                    "Nombre",
                    "Precio Referencia",
                    "Proveedor"
            };

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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar materia prima:\n" + e.getMessage());
        }
    }

    private void actualizar() {
        try {
            int idMateriaPrima = Integer.parseInt(txtIdMateriaPrima.getText().trim());
            BigDecimal precio = new BigDecimal(txtPrecioReferencia.getText().trim());

            dao.actualizarMateriaPrima(
                    idMateriaPrima,
                    txtNombreMateriaPrima.getText().trim(),
                    precio
            );

            JOptionPane.showMessageDialog(this,
                    "Materia prima actualizada correctamente.");

            cargar();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar materia prima:\n" + e.getMessage());
        }
    }

    private void eliminar() {
        try {
            int idMateriaPrima = Integer.parseInt(txtIdMateriaPrima.getText().trim());

            dao.eliminarMateriaPrima(idMateriaPrima);

            JOptionPane.showMessageDialog(this,
                    "Materia prima eliminada correctamente.");

            cargar();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar materia prima:\n" + e.getMessage());
        }
    }

    private void mostrarPromedio() {
        try {
            BigDecimal promedio = dao.obtenerCostoPromedio();

            JOptionPane.showMessageDialog(this,
                    "Costo promedio de materias primas: " + promedio);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener costo promedio:\n" + e.getMessage());
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