package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnection implements DatabaseConnection {

    // Static variables for credentials
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    // Static block to load credentials from config.properties exactly once
    static {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
            URL = props.getProperty("db.url");
            USERNAME = props.getProperty("db.username");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Critical Error: Could not read config.properties file. Ensure it is in the project root.");
            e.printStackTrace();
        }
    }

    private Connection connection;

    // Constructor establishes the connection
    public SQLConnection() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. Check your network, VPN, and credentials.");
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection safely closed.");
            } catch (SQLException e) {
                System.err.println("Error closing the database connection.");
                e.printStackTrace();
            }
        }
    }
}