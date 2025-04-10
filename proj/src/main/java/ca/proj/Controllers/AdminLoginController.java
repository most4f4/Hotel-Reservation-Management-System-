package ca.proj.Controllers;

import ca.proj.Database.Database;
import ca.proj.HotelReservationApplication;
import ca.proj.Utility.AlertHelper;
import ca.proj.Utility.LoggerUtil;
import ca.proj.Utility.Rules;
import ca.proj.Utility.SceneName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class AdminLoginController {

    @FXML private Button loginBtn;
    @FXML private PasswordField passwordPF;
    @FXML private TextField usernameTF;
    private static final Logger logger = LoggerUtil.getLogger();

    @FXML void homeBtnPressed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca/proj/Views/welcome-view.fxml"));
            Parent root = loader.load();
            Stage currentStrage = (Stage) loginBtn.getScene().getWindow();
            currentStrage.setTitle("Welcome!");
            currentStrage.setScene(new Scene(root));
        } catch (IOException e) {
            logger.severe("Error loading Welcome Page: " + e.getMessage());
        }
    }

    @FXML
    void loginPressed(ActionEvent event) {
        String username = usernameTF.getText().trim();
        String password = passwordPF.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showErrorAlert("Error", "All fields (username & password) are required!");
            logger.warning("Login failed: Empty fields.");
            return;
        }

        Database db = Database.getInstance();
        if (db.authenticateLogin(username, password)) {
            logger.info("Admin " + username + " logged in successfully.");
            resetFields();
            getStage().setTitle("Admin Dashboard");
            getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));
        } else {
            AlertHelper.showErrorAlert("Error", "Invalid Username or Password!");
            logger.warning("Login failed: Invalid credentials for username " + username);
            usernameTF.requestFocus();
        }
    }

    private Stage getStage() {
        return (Stage) usernameTF.getScene().getWindow();
    }

    public void resetFields() {
        usernameTF.setText("");
        passwordPF.setText("");
    }

    @FXML
    void rulesBtnPressed(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

}
