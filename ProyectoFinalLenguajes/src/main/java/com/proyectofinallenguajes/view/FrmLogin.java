package com.proyectofinallenguajes.view;

import javax.swing.*;
import java.awt.Image;

import com.proyectofinallenguajes.dao.usuarioDAO;
import com.proyectofinallenguajes.model.usuario;

public class FrmLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;

    private usuarioDAO dao = new usuarioDAO();

    public FrmLogin() {
        setTitle("Login");
        setSize(360, 360); 
        setLayout(null);
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(
                getClass().getResource("/com/proyectofinallenguajes/img/logo.png")
        );

        Image imgOriginal = icon.getImage();
        Image imgEscalada = imgOriginal.getScaledInstance(-1, 120, Image.SCALE_SMOOTH);

        JLabel lblLogo = new JLabel(new ImageIcon(imgEscalada));
        lblLogo.setBounds(80, 10, 200, 120); 

 
        JLabel lblCorreo = new JLabel("Correo");
        JLabel lblPass = new JLabel("Contraseña");

        txtCorreo = new JTextField();
        txtContrasena = new JPasswordField();
        btnLogin = new JButton("Ingresar");

    
        lblCorreo.setBounds(40, 150, 100, 20);
        txtCorreo.setBounds(140, 150, 180, 25);

        lblPass.setBounds(40, 190, 100, 20);
        txtContrasena.setBounds(140, 190, 180, 25);

        btnLogin.setBounds(110, 250, 140, 35);

        add(lblLogo);
        add(lblCorreo);
        add(txtCorreo);
        add(lblPass);
        add(txtContrasena);
        add(btnLogin);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        try {
            usuario u = dao.login(
                    txtCorreo.getText().trim(),
                    new String(txtContrasena.getPassword())
            );

            if (u == null) {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
                return;
            }

            if (u.getId_rol() != 1) {
                JOptionPane.showMessageDialog(this,
                        "Acceso denegado. Solo administradores.");
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Bienvenido " + u.getNombre());

            new MenuPrincipal().setVisible(true);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error:\n" + e.getMessage());
        }
    }
}