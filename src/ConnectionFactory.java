package src;

public class ConnectionFactory {

    public static DatabaseConnection createConnection(String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        
        if ("SQL".equalsIgnoreCase(type)) {
            return new SQLConnection();
        }
        
        // Future extensions (e.g., NoSQLConnection) can go here
        return null;
    }
}