package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Models.Room;
import ca.proj.Utility.*;
import ca.proj.Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AdminBookinTwoRoomSelection implements Initializable {

    @FXML private Label CheckInDate;
    @FXML private Label CheckOutDate;
    @FXML private Label numGuestsLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Label totalPayableLabel;
    @FXML private Label suggestionLabel;
    @FXML private Button nextBtn;
    @FXML private TableView<Room> availableRoomsTV;
    @FXML private TableView<Room> selectedRoomsTV;
    @FXML private TableColumn<Room, String> roomNumTC1;
    @FXML private TableColumn<Room, String> roomTypeTC1;
    @FXML private TableColumn<Room, String> pricePerNightTC1;
    @FXML private TableColumn<Room, String> roomNumTC2;
    @FXML private TableColumn<Room, String> roomTypeTC2;
    @FXML private TableColumn<Room, String> pricePerNightTC2;


    private static final Logger logger = LoggerUtil.getLogger();
    private LocalDate checkIn, checkOut;
    private int numGuests;
    private int minRoomsNeeded;
    private double tax;
    private double amount;
    private int nights;

    public void setBookingDetails(LocalDate checkIn, LocalDate checkOut, int numGuests) {
        CheckInDate.setText(String.valueOf(checkIn));
        CheckOutDate.setText(String.valueOf(checkOut));
        numGuestsLabel.setText(String.valueOf(numGuests));

        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numGuests = numGuests;
        this.nights = (int) (checkOut.toEpochDay() - checkIn.toEpochDay());

        // Suggest rooms based on capacity rules
        suggestRooms(numGuests);
        loadAvailableRooms();
    }

    private void suggestRooms(int numGuests) {
        int doubleRooms = 0;
        int otherRooms = 0;
        String suggestion = "";


        if (numGuests >= 1 && numGuests <= 2) {
            otherRooms = 1;
            suggestion = "1 Single/Deluxe/Penthouse";
            minRoomsNeeded = 1;
        } else if (numGuests >= 3 && numGuests <= 4) {
            doubleRooms = 1;
            suggestion = "1 Double";
            minRoomsNeeded = 1;
        } else if (numGuests >= 5 && numGuests <= 6) {
            doubleRooms = 1;
            otherRooms = 1;
            suggestion = "1 Double + 1 Single/Deluxe/Penthouse";
            minRoomsNeeded = 2;
        } else if (numGuests >= 7 && numGuests <= 8) {
            doubleRooms = 2;
            suggestion = "2 Doubles";
            minRoomsNeeded = 2;
        } else {
            // For 9+ guests, extend the pattern (e.g., 9-10: 2 Doubles + 1 Single)
            doubleRooms = numGuests / 4;
            int remaining = numGuests % 4;
            otherRooms = (remaining + 1) / 2;
            suggestion = doubleRooms + " Double" + (doubleRooms > 1 ? "s" : "") +
                    (otherRooms > 0 ? " + " + otherRooms + " Single/Deluxe/Penthouse" : "");
            minRoomsNeeded = doubleRooms + otherRooms;
        }

        suggestionLabel.setText("Suggested: " + suggestion + " (" + minRoomsNeeded + " rooms)");
    }

    private void loadAvailableRooms() {
        Database db = Database.getInstance();
        List<Room> rooms = db.getAvailableRoomsForDates(checkIn, checkOut);
        availableRoomsTV.getItems().clear();
        availableRoomsTV.getItems().addAll(rooms);
        selectedRoomsTV.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure availableRoomsTV columns
        roomNumTC1.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        roomTypeTC1.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        pricePerNightTC1.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Configure selectedRoomsTV columns
        roomNumTC2.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        roomTypeTC2.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        pricePerNightTC2.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Sort availableRoomsTV by roomID (ascending)
        availableRoomsTV.getSortOrder().add(roomNumTC1);
        roomNumTC1.setSortType(TableColumn.SortType.ASCENDING);
    }

    @FXML void nextBtnPressed(ActionEvent event) {
        int selectedCount = selectedRoomsTV.getItems().size();
        int totalCapacity = calculateTotalCapacity();

        // Validation
        if (selectedCount < minRoomsNeeded) {
            AlertHelper.showErrorAlert("Too Few Rooms", "You need to select at least " + minRoomsNeeded + " room" +
                    (minRoomsNeeded > 1 ? "s" : "") + " for " + numGuests + " guests.");
            return;
        }

        if (selectedCount > numGuests) {
            AlertHelper.showErrorAlert("Too many Rooms", "You cannot select more than " + numGuests + " room" +
                    (numGuests > 1 ? "s" : "") + " for " + numGuests + " guests.");
            return;
        }

        if (totalCapacity < numGuests) {
            AlertHelper.showErrorAlert("Insufficient Capacity", "The selected rooms (capacity: " + totalCapacity +
                    ") cannot accommodate " + numGuests + " guests.");
            return;
        }


        AdminBooklingThreeGuestDetails controller = (AdminBooklingThreeGuestDetails) HotelReservationApplication.getControllers().get(SceneName.Admin_Booking_GuestDetails);
        controller.setReservationDetails(checkIn, checkOut, numGuests, selectedRoomsTV.getItems(), amount, tax);
        getStage().setTitle("Guest Details");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Admin_Booking_GuestDetails));
    }

    private int calculateTotalCapacity() {
        int capacity = 0;
        for (Room room : selectedRoomsTV.getItems()) {
            if (room.getRoomType() == RoomType.DOUBLE) {
                capacity += 4;
            } else {
                capacity += 2; // Single, Deluxe, Penthouse
            }
        }
        return capacity;
    }

    @FXML void selectButtonPressed(ActionEvent event) {
        // Get the single selected room from availableRoomsTV
        Room selectedRoom = availableRoomsTV.getSelectionModel().getSelectedItem();

        if (selectedRoom == null) {
            return; // No room selected, do nothing
        }

        // Move the selected room to selectedRoomsTV and remove from availableRoomsTV
        selectedRoomsTV.getItems().add(selectedRoom);
        availableRoomsTV.getItems().remove(selectedRoom);

        // Update cost labels
        updateCostLabels();
    }

    private void updateCostLabels() {
        double totalCost = 0.0;

        // Calculate total cost based on selected rooms
        for (Room room : selectedRoomsTV.getItems()) {
            totalCost += room.getPrice() * nights;
        }

        double tax = totalCost * 0.13; // 13% HST/GST
        double totalPayable = totalCost + tax;

        // Update labels with formatted values
        totalLabel.setText(String.format("%.2f", totalCost));
        taxLabel.setText(String.format("%.2f", tax));
        totalPayableLabel.setText(String.format("%.2f", totalPayable));
        this.amount = totalCost;
        this.tax = tax;
    }

    @FXML void removeButtonPressed(ActionEvent event) {
        // Get the single selected room from availableRoomsTV
        Room selectedRoom = selectedRoomsTV.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            return; // No room selected, do nothing
        }
        selectedRoomsTV.getItems().remove(selectedRoom);
        availableRoomsTV.getItems().add(selectedRoom);

        availableRoomsTV.sort(); // Trigger sort after adding
        updateCostLabels(); // Recalculate costs after removal
    }

    @FXML void homeBtnPressed(ActionEvent event) {
        resetFields();
        getStage().setTitle("Admin Dashboard");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));
    }

    @FXML void previousBtnPressed(ActionEvent event) {
        getStage().setTitle("Reservation Details");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Admin_Booking_ReservationDetails));
    }

    @FXML void ruleBtnPressed(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

    @FXML void sigOutBrnPressed(ActionEvent event) {
        WindlowLoader.signOutProcess(getStage());
    }

    public Stage getStage() {
        return (Stage) availableRoomsTV.getScene().getWindow();
    }

    public void resetFields() {
        // Reset labels
        CheckInDate.setText("");
        CheckOutDate.setText("");
        numGuestsLabel.setText("");
        taxLabel.setText("0.00");
        totalLabel.setText("0.00");
        totalPayableLabel.setText("0.00");
        suggestionLabel.setText("");

        // Clear table views
        availableRoomsTV.getItems().clear();
        selectedRoomsTV.getItems().clear();

        // Reset instance variables
        checkIn = null;
        checkOut = null;
        numGuests = 0;
        minRoomsNeeded = 0;
    }


}
