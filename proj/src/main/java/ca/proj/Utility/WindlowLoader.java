package ca.proj.Utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class WindlowLoader {
    private static final Logger logger = LoggerUtil.getLogger();

    public static void loadPage (Stage stage, String path, String title){
        try {
            FXMLLoader loader = new FXMLLoader(WindlowLoader.class.getResource(path));
            Parent root = loader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            logger.severe("Error loading: " + e.getMessage());
        }
    }

    public static void signOutProcess(Stage stage){
        boolean confirm = AlertHelper.showConfirmationAlert("Sign out", "Are you sure to sign out?");
        if (confirm) {
            WindlowLoader.loadPage(stage ,"/ca/proj/Views/welcome-view.fxml","Welcome!");
        }
    }
}
