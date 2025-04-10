package ca.proj.ServerClient;

import ca.proj.Database.Database;
import ca.proj.Utility.LoggerUtil;

import java.io.*;
import java.net.*;

public class AdminHandler implements Runnable {
    private final Socket socket;
    private Database db;
    private PrintWriter out;
    private BufferedReader in;
    private String adminId;

    public AdminHandler(Socket socket, Database db) {
        this.socket = socket;
        this.db = db;
    }

    @Override public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Login process
            if (!login()) {
                out.println("Login failed. Closing connection.");
                socket.close();
                return;
            }

            String command;
            // Main loop to handle admin commands
            while ((command = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(command)) {
                    out.println("Goodbye.");
                    break;
                }
                processCommand(command);
            }
        } catch (IOException e) {
            LoggerUtil.getLogger().severe("Error in AdminSession: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LoggerUtil.getLogger().severe("Error closing socket: " + e.getMessage());
            }
        }
    }

    private boolean login() throws IOException {
        LoggerUtil.getLogger().info("Prompting for username");
        out.println("Enter Admin Username:");
        out.flush();
        String username = in.readLine();
        if (username == null || username.trim().isEmpty()) {
            LoggerUtil.getLogger().warning("No username received. Client disconnected?");
            return false;
        }
        LoggerUtil.getLogger().info("Received username: " + username);

        LoggerUtil.getLogger().info("Prompting for password");
        out.println("Enter Password:");
        out.flush();
        String password = in.readLine();
        if (password == null || password.trim().isEmpty()) {
            LoggerUtil.getLogger().warning("No password received for " + username + ". Client disconnected?");
            return false;
        }
        LoggerUtil.getLogger().info("Received password for " + username);


        // Simple validation (replace with your Admin class logic)
        if (db.authenticateLogin(username, password)) {
            this.adminId = username;
            out.println("Login successful. Welcome, " + adminId);
            return true;
        }
        out.println("Login failed. Try again.");
        return false;
    }

    private void processCommand(String command) {
        String[] parts = command.split(" ", 2);
        String action = parts[0].toLowerCase();

        switch (action) {
            case "search":
                if (parts.length > 1) {
                    searchGuest(parts[1]);
                } else {
                    out.println("Usage: search <guest_name>");
                }
                out.println("Done"); // Signal end of response
                out.flush();
                break;
            case "exit":
                out.println("Goodbye.");
                break;
            default:
                out.println("Unknown command. Available: search, exit");
        }
    }

    private void searchGuest(String name) {
        // Simplified search (adapt to your Database method)
        String result = db.searchGuestByName(name);
        out.println(result != null ? result : "Guest not found.");
        out.flush();
    }
}
