// Written by Sam Good

package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

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

    // Singleton instance and the actual SQL connection
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor to establish the connection
    private DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. Check your network, VPN, and credentials.");
            e.printStackTrace();
        }
    }

    // Public method to retrieve the single instance
    public static DatabaseConnection getInstance() throws SQLException {
        // If the instance doesn't exist, or if the connection was lost/closed, create a new one
        if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Public method to expose the Connection object to DAOs/Controllers
    public Connection getConnection() {
        return connection;
    }

    // Utility method to safely close the connection when the app shuts down
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
