package com.proyectofinallenguajes.conexion;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection connection;
    private static final Dotenv dotenv = Dotenv.load();

    public static Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()) {

            System.setProperty("oracle.net.tns_admin", dotenv.get("WALLET_PATH"));

            Properties properties = new Properties();
            properties.setProperty("user", dotenv.get("DB_USER"));
            properties.setProperty("password", dotenv.get("DB_PASSWORD"));

            connection = DriverManager.getConnection(dotenv.get("DB_URL"), properties);
        }
        return connection;
    }
}
