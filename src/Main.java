package src;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Initializing system...");
        DatabaseConnection dbConnection = ConnectionFactory.createConnection("SQL");

        if (dbConnection == null || dbConnection.getConnection() == null) {
            System.out.println("Fatal Error: Could not connect to the database. Exiting application.");
            return;
        }

        DBOperation dbOps = new SQLOperation(dbConnection);
        Service service = new Service(dbOps);

        try (Scanner scanner = new Scanner(System.in)) {
            SocialController controller = new SocialController(service, scanner);
            controller.run();
        } finally {
            dbConnection.closeConnection();
        }
    }
}

