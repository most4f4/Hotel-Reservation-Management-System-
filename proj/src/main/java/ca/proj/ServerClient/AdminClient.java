package ca.proj.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AdminClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server. Follow prompts to login.");

            String serverMessage;
            boolean loginComplete = false;

            // Login loop with debugging
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
                if (serverMessage.equals("Enter Admin Username:")) {
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    out.println(username);
                    out.flush();
                } else if (serverMessage.equals("Enter Password:")) {
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    out.println(password);
                    out.flush();
                } else if (serverMessage.startsWith("Login successful")) {
                    loginComplete = true;
                    break;
                } else if (serverMessage.contains("failed") || serverMessage.contains("Invalid")) {
                    System.out.println("Login failed. Exiting.");
                    return;
                }
            }

            if (!loginComplete) {
                System.out.println("Login process did not complete. Exiting.");
                return;
            }

            // Command loop
            while (true) {
                System.out.print("Enter command (search <guest name>, exit): ");
                String command = scanner.nextLine();
                out.println(command);
                out.flush();
                if (command.equals("exit")) {
                    System.out.println(in.readLine()); // Print "Goodbye"
                    break;
                }

                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                    if (serverMessage.equals("Done")) break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}