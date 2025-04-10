package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class AdminBookingOneReservationDetails {

    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;
    @FXML private ComboBox<Integer> numGuests;
    @FXML private Button nextButton;

    private static final Logger logger = LoggerUtil.getLogger();

    public void initialize() {
        nextButton.setDisable(true);
        numGuests.getItems().addAll(1,2,3,4,5,6,7,8);
        numGuests.valueProperty().addListener(((observableValue, oldVal, newVal) -> {
            updateButtons();
        }));
        checkInDate.valueProperty().addListener(((observableValue, oldVal, newVal) -> {
            updateButtons();
        }));
        checkOutDate.valueProperty().addListener(((observableValue, oldVal, newVal) -> {
            updateButtons();
        }));
    }

    private void updateButtons(){
        nextButton.setDisable(numGuests.getValue() == null || checkInDate.getValue() == null || checkOutDate.getValue() == null);
    }

    @FXML void nextBtnPressed(ActionEvent event) {
        try {
            if (checkInDate.getValue().isAfter(checkOutDate.getValue())){
                AlertHelper.showErrorAlert("Invalid dates", "Check out date should be after the check in date.");
                logger.severe("Invalid booking details");
                throw new IllegalArgumentException("Invalid dates or number of guests");
            }
            AdminBookinTwoRoomSelection controller = (AdminBookinTwoRoomSelection) HotelReservationApplication.getControllers().get(SceneName.Admin_Booking_RoomSelection);
            controller.setBookingDetails(checkInDate.getValue(), checkOutDate.getValue(), numGuests.getValue());
            getStage().setTitle("Room Selection");
            getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Admin_Booking_RoomSelection));

        } catch (Exception e) {
            logger.severe("Error in booking details: " + e.getMessage());
        }
    }

    @FXML void previousBtnPressed(ActionEvent event) {
        resetFields();
        getStage().setTitle("Admin Dashboard");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));
    }

    @FXML void rulesBtnPressed(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

    @FXML void signOutBtnPressed(ActionEvent event) {
        WindlowLoader.signOutProcess(getStage());
    }

    public Stage getStage() {
        return (Stage) checkInDate.getScene().getWindow();
    }

    public void resetFields() {
        // Reset ComboBox selection
        numGuests.getSelectionModel().clearSelection();
        numGuests.setValue(null);

        // Reset DatePickers
        checkInDate.setValue(null);
        checkOutDate.setValue(null);
    }


}
