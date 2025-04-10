package ca.proj.ServerClient;

import ca.proj.Database.Database;
import ca.proj.Utility.LoggerUtil;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class AdminServer {
    private static final int PORT = 12345;  // Server port
    private static final Logger logger = LoggerUtil.getLogger();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);  // Thread pool for handling multiple connections
    private Database db;

    public AdminServer() {
        this.db = Database.getInstance();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server started on port \"" + PORT +". Waiting for admin connections...");
            System.out.println("Server started on port \"" + PORT +". Waiting for admin connections...");

            // Accept connections from admins
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New admin connected: " + clientSocket.getInetAddress());

                // Handle each connection in a separate thread
                executor.submit(new AdminHandler(clientSocket, db));
                //AdminHandler handler = new AdminHandler(clientSocket, db);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AdminServer().start();
    }
}
