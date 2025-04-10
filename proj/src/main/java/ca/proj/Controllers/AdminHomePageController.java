package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Utility.LoggerUtil;
import ca.proj.Utility.Rules;
import ca.proj.Utility.SceneName;
import ca.proj.Utility.WindlowLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class AdminHomePageController {

    private static final Logger logger = LoggerUtil.getLogger();

    @FXML private Button signOutBtn;

    @FXML void availableRoomsBtnPressed(ActionEvent event) {
        WindlowLoader.loadPage(getStage(), "/ca/proj/Views/admin-availableRooms.fxml", "Available Rooms" );
    }

    @FXML void billServiceBtnPressed(ActionEvent event) {
        WindlowLoader.loadPage(getStage(), "/ca/proj/Views/admin-billingService.fxml", "Billing Service");
    }

    @FXML void bookRoomBtnPressed(ActionEvent event) {
        getStage().setTitle("Reservation Details");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Admin_Booking_ReservationDetails));
    }

    @FXML void currentBookingsBtnPressed(ActionEvent event) {
        WindlowLoader.loadPage(getStage(), "/ca/proj/Views/admin-currentBookings.fxml", "Current Bookings" );
    }

    @FXML void reviewsBtnPressed(ActionEvent event) {
        WindlowLoader.loadPage(getStage(), "/ca/proj/Views/admin-reviews.fxml", "Reviews");
    }

    @FXML void rulesBtnPressed(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

    @FXML void searchBtnPressed(ActionEvent event) {
        WindlowLoader.loadPage(getStage(), "/ca/proj/Views/admin-search.fxml", "All Reservations" );
    }

    @FXML void signOutBtnPressed(ActionEvent event) {
        WindlowLoader.signOutProcess(getStage());
    }

    private Stage getStage(){
        return (Stage) signOutBtn.getScene().getWindow();
    }

}
