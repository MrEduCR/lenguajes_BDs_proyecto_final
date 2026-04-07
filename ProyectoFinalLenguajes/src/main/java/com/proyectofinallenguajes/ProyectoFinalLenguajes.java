package com.proyectofinallenguajes;

import java.sql.Connection;
import java.sql.SQLException;

import com.proyectofinallenguajes.conexion.DatabaseConnection;

public class proyectoFinalLenguajes {

    public static void main(String[] args) {
        //Asegurarse de tener version jdk 17
        System.out.println(System.getProperty("java.version"));

        try {
            Connection conn = DatabaseConnection.getConnection();
            
            System.out.println("Connection successfull!"); 
            
            conn.close();
        } catch(SQLException e){
            System.out.println("Connection Failed:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
}
