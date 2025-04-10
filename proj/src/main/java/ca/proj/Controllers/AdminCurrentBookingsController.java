package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Models.Feedback;
import ca.proj.Models.Reservation;
import ca.proj.Utility.*;
import ca.proj.Database.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AdminCurrentBookingsController implements Initializable {

    @FXML private TableView<Reservation> currentBookingsTV;
    @FXML private TableColumn<Reservation, String> bookingIDTC;
    @FXML private TableColumn<Reservation, String> guestNameTC;
    @FXML private TableColumn<Reservation, String> roomsTC;
    @FXML private TableColumn<Reservation, Integer> numNightsTC;
    @FXML private TableColumn<Reservation, LocalDate> checkInTC;

    @FXML private TextField searchTF;
    private static final Logger logger = LoggerUtil.getLogger();
    private ObservableList<Reservation> allBookings;
    private Database db;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        db = Database.getInstance();

        // Set up TableView columns
        bookingIDTC.setCellValueFactory(new PropertyValueFactory<>("reservationID"));

        guestNameTC.setCellValueFactory(cellData -> {
            Reservation res = cellData.getValue();
            return new SimpleStringProperty(res.getGuestName(db));
        });

        roomsTC.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getRoomsString());
        });
        numNightsTC.setCellValueFactory(new PropertyValueFactory<>("numNights"));
        checkInTC.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        TableColumnModifier.centerAlignColumn(bookingIDTC);
        TableColumnModifier.centerAlignColumn(guestNameTC);
        TableColumnModifier.centerAlignColumn(checkInTC);
        TableColumnModifier.centerAlignColumn(roomsTC);
        TableColumnModifier.centerAlignColumn(numNightsTC);

        // Load initial data
        loadBookings();

        // Search functionality
        searchTF.textProperty().addListener((obs, oldValue, newValue) -> filterBookings(newValue.trim()));

        currentBookingsTV.setOnMouseClicked(e->{
            if (e.getClickCount() == 2){
                handleDoubleClick(e);
            }
        });
    }

    private void filterBookings(String searchText) {
        if (searchText.isEmpty()) {
            currentBookingsTV.setItems(allBookings);
        } else {
            ObservableList<Reservation> filtered = FXCollections.observableArrayList();
            for (Reservation res : allBookings) {
                if (res.getGuestName(db).toLowerCase().contains(searchText.toLowerCase())) {
                    filtered.add(res);
                }
            }
            currentBookingsTV.setItems(filtered);
        }
    }

    private void loadBookings() {
        allBookings = FXCollections.observableArrayList(db.getCurrentBookings());
        currentBookingsTV.setItems(allBookings);
    }

    @FXML void checkinPressed(ActionEvent event) {
        Reservation selected = currentBookingsTV.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showErrorAlert("Error", "Please select a booking to check in.");
            return;
        }
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm Check In",
                "Are you sure you want to check in " + selected.getGuestName(db) + "?");
        if (confirmed) {
            if (db.checkIn(selected.getReservationID())) {
                loadBookings();
                AlertHelper.showInformationAlert("Success", "Guest checked in successfully.");
            } else {
                AlertHelper.showErrorAlert("Error", "Failed to check in guest.");

            }
        }
    }

    @FXML void deleteBtnPressed(ActionEvent event) {
        Reservation selected = currentBookingsTV.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showErrorAlert("Error", "Please select a booking to delete.");
            return;
        }
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm Delete",
                "Are you sure you want to delete booking " + selected.getReservationID() + "?");
        if (confirmed) {
            if (db.deleteBooking(selected.getReservationID())) {
                allBookings.remove(selected);
                currentBookingsTV.refresh();
                AlertHelper.showInformationAlert("Success", "Booking deleted successfully.");
            } else {
                AlertHelper.showErrorAlert("Error", "Failed to delete booking.");
            }
        }
    }

    @FXML void cancelBookingPressed(ActionEvent event) {
        Reservation selected = currentBookingsTV.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showErrorAlert("Error", "Please select a booking to delete.");
            return;
        }
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm Cancellation",
                "Are you sure you want to cancel " + selected.getGuestID() + "?");
        if (confirmed) {
            if (db.cancelBooking(selected.getReservationID())) {
                loadBookings();
                AlertHelper.showInformationAlert("Success", "Booking cancelled successfully.");
            } else {
                AlertHelper.showErrorAlert("Error", "Failed to cancel B=booking.");

            }
        }
    }

    public void handleDoubleClick(MouseEvent e){
        Reservation res = currentBookingsTV.getSelectionModel().getSelectedItem();
        if (res != null) {
            showReservationDetailsDialog(res);
        }
    }

    private void showReservationDetailsDialog(Reservation reservation) {
        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Feedback Details");
        dialog.setHeaderText("Details for Reservation ID: " + reservation.getReservationID());

        // Set button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        // Create grid for details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        // Add reservation details
        grid.add(new Label("Reservation ID:"), 0, 0);
        grid.add(new Label(reservation.getReservationID()), 1, 0);
        grid.add(new Label("Guest Name:"), 0, 1);
        grid.add(new Label(reservation.getGuestName(db)), 1, 1);
        grid.add(new Label("Rooms:"), 0, 2);
        grid.add(new Label(reservation.getRoomsString()), 1, 2);
        grid.add(new Label("Check-In Date:"), 0, 3);
        grid.add(new Label(reservation.getCheckInDate().toString()), 1, 3);
        grid.add(new Label("Check-out Date:"), 0, 4);
        grid.add(new Label(reservation.getCheckOutDate().toString()), 1, 4);
        grid.add(new Label("Number of Nights:"), 0, 5);
        grid.add(new Label(reservation.getNumNights().toString()), 1, 5);
        grid.add(new Label("Number of Guests:"), 0, 6);
        grid.add(new Label(reservation.getGuestName(db)), 1, 6);
        grid.add(new Label("Total Payable:"), 0, 7);
        grid.add(new Label(reservation.getTotalPrice(db).toString()), 1, 7);
        grid.add(new Label("Current Status:"), 0, 8);
        grid.add(new Label(reservation.getStatus()), 1, 8);

        dialog.getDialogPane().setContent(grid);

        // Handle result
        dialog.showAndWait();
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
        resetFields();
        WindlowLoader.signOutProcess(getStage());
    }

    public Stage getStage() {
        return (Stage) currentBookingsTV.getScene().getWindow();
    }

    public void resetFields() {
        searchTF.setText("");
    }


}
