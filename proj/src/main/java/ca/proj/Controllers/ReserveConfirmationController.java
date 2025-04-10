package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Utility.LoggerUtil;
import ca.proj.Utility.Rules;
import ca.proj.Utility.SceneName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class ReserveConfirmationController {

    @FXML private Label reservationIDLabel;
    @FXML private Label checkOutDateLabel;
    @FXML private Label checkinDateLabel;
    @FXML private Label emailLabel;
    @FXML private Label nameLabel;
    @FXML private Label numGuestsLabel;
    @FXML private Label payableLabel;
    @FXML private Label phoneLabel;
    @FXML private Label roomLabel;

    private static final Logger logger = LoggerUtil.getLogger(); // Use shared logger


    public void setFields(String reservationID, String name, String email, String phone
    , String numGuests, String checkin, String checkout, String rooms, double totalPayable) {
        reservationIDLabel.setText(reservationID);
        nameLabel.setText(name);
        emailLabel.setText(email);
        phoneLabel.setText(phone);
        numGuestsLabel.setText(numGuests);
        checkinDateLabel.setText(checkin);
        checkOutDateLabel.setText(checkout);
        roomLabel.setText(rooms);
        payableLabel.setText(String.valueOf(totalPayable));
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        GetInfoController getInfoController = (GetInfoController) HotelReservationApplication.getControllers().get(SceneName.ReservationDetails);
        getInfoController.resetFields();

        RoomSelectionController roomSelectionController = (RoomSelectionController) HotelReservationApplication.getControllers().get(SceneName.RoomSelection);
        roomSelectionController.resetFields();

        GuestDetailsController guestDetailsController = (GuestDetailsController) HotelReservationApplication.getControllers().get(SceneName.GuestDetails);
        guestDetailsController.resetFields();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca/proj/Views/welcome-view.fxml"));
            Parent root = loader.load();
            getStage().setTitle("Welcome!");
            getStage().setScene(new Scene(root));
        } catch (IOException e) {
            logger.severe("Error loading RoomSelectionController: " + e.getMessage());
        }
    }

    @FXML
    void rulesBtn(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

    public Stage getStage() {
        return (Stage) nameLabel.getScene().getWindow();
    }


}
