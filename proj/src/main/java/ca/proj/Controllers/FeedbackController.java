package ca.proj.Controllers;

import ca.proj.Models.Feedback;
import ca.proj.Utility.AlertHelper;
import ca.proj.Utility.LoggerUtil;
import ca.proj.Utility.Rules;
import ca.proj.Database.Database;
import ca.proj.Utility.WindlowLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class FeedbackController {

    @FXML private TextArea feedbackTA;
    @FXML private TextField reservationIDTF;
    @FXML private Slider rateSlider;

    private static final Logger logger = LoggerUtil.getLogger();

    public void initialize(){
        this.rateSlider.setValue(5
        );
    }

    @FXML void previousBtnPressed(ActionEvent event) {
        resetFields();
        WindlowLoader.loadPage(getStage() ,"/ca/proj/Views/welcome-view.fxml","Welcome!");
    }

    @FXML void rulesBtnPressed(ActionEvent event) {Rules.rulesButtonPressed();}

    @FXML void submitBtnPressed(ActionEvent event) {
        String reservationID = reservationIDTF.getText().trim();
        int rate = (int) rateSlider.getValue();
        String comment = feedbackTA.getText().trim();

        // Basic validation
        if (reservationID.isEmpty() || comment.isEmpty()) {
            AlertHelper.showErrorAlert("Error", "All fields (Booking ID, Feedback) are required!");
            logger.warning("Feedback submission failed: Empty fields.");
            return;
        }

        Database db = Database.getInstance();
        Feedback newFeedback = new Feedback(reservationID, rate, comment);
        if (db.saveFeedback(newFeedback)) {
            AlertHelper.showInformationAlert("Feedback Saved", "Thank you for your time and feedback!");
            resetFields();
            WindlowLoader.loadPage(getStage() ,"/ca/proj/Views/welcome-view.fxml","Welcome!");

        } else {
            AlertHelper.showErrorAlert("Error", "Feedback submission failed: Invalid reservation ID.");
            logger.warning("Feedback submission failed: Invalid reservation ID.");
        }
    }

    public void resetFields() {
        reservationIDTF.setText("");
        rateSlider.setValue(5);
        feedbackTA.setText("");
    }

    private Stage getStage(){
        return (Stage) feedbackTA.getScene().getWindow();
    }

}
