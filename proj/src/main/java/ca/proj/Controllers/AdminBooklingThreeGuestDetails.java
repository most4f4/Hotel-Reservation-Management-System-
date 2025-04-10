package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Models.Billing;
import ca.proj.Models.Guest;
import ca.proj.Models.Reservation;
import ca.proj.Models.Room;
import ca.proj.Utility.*;
import ca.proj.Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class AdminBooklingThreeGuestDetails {

    @FXML private TextField addressTF;
    @FXML private TextField emailTF;
    @FXML private TextField nameTF;
    @FXML private TextField phoneTF;
    @FXML private Button submitButton;

    private static final Logger logger = LoggerUtil.getLogger();
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numGuests;
    private List<Room> selectedRooms;
    private double amount;
    private double tax;


    public void setReservationDetails(LocalDate checkIn, LocalDate checkOut, int numGuests, List<Room> selectedRooms, double amount, double tax) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numGuests = numGuests;
        this.selectedRooms = selectedRooms;
        this.amount = amount;
        this.tax= tax;
    }

    public void initialize(){
        submitButton.setDisable(true);
        nameTF.textProperty().addListener((obs, oldVal, newVal) -> updateButtons());
        phoneTF.textProperty().addListener((obs, oldVal, newVal) -> updateButtons());
        emailTF.textProperty().addListener((obs, oldVal, newVal) -> updateButtons());
        addressTF.textProperty().addListener((obs, oldVal, newVal) -> updateButtons());
    }

    private void updateButtons(){
        submitButton.setDisable(nameTF.getText().trim().isEmpty() || phoneTF.getText().trim().isEmpty() || emailTF.getText().trim().isEmpty() ||
                addressTF.getText().trim().isEmpty());
    }

    private boolean validate(){
        String name = nameTF.getText().trim();
        String phoneNumber = phoneTF.getText().trim();
        String email = emailTF.getText().trim();
        String address = addressTF.getText().trim();

        StringBuilder errorMessage = new StringBuilder();

        // Name validation
        if (name.isEmpty()) {
            errorMessage.append("- Name is required.\n");
        } else if (!name.matches("[a-zA-Z\\s]+")) {
            errorMessage.append("- Name must contain only letters and spaces.\n");
        }

        // Phone number validation
        if (phoneNumber.isEmpty()) {
            errorMessage.append("- Phone number is required.\n");
        } else if (!phoneNumber.matches("\\d{10}")) { // Example: 10-digit phone number
            errorMessage.append("- Phone number must be 10 digits (e.g., 1234567890).\n");
        }

        // Email validation
        if (email.isEmpty()) {
            errorMessage.append("- Email is required.\n");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // Basic email regex
            errorMessage.append("- Email must be valid (e.g., user@example.com).\n");
        }

        // Address validation (optional field, but if provided, ensure it's not just whitespace)
        if (!address.isEmpty() && address.trim().length() < 5) {
            errorMessage.append("- Address, if provided, must be at least 5 characters.\n");
        }

        if (errorMessage.length() > 0) {
            String errors = errorMessage.toString();
            AlertHelper.showErrorAlert("Validation Error", "Please correct the following:\n" + errors);
            logger.warning("Guest details validation failed: " + errors.replace("\n", " | "));
            return false;
        }
        logger.info("Guest details validated successfully for reservation.");
        return true;

    };


    @FXML void homeBtnPressed(ActionEvent event) {
        resetFields();
        getStage().setTitle("Admin Dashboard");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));
    }

    @FXML void previousBtnPressed(ActionEvent event) {
        getStage().setTitle("Room Selection");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Admin_Booking_RoomSelection));
    }

    @FXML void rulesBtnPressed(ActionEvent event) {Rules.rulesButtonPressed();}

    @FXML void signOutBtnPressed(ActionEvent event) {
        WindlowLoader.signOutProcess(getStage());
    }

    @FXML void submitButtonPressed(ActionEvent event) {
        if (!validate()) {
            return; // Stop if validation fails
        }

        String name = nameTF.getText().trim();
        String phoneNumber = phoneTF.getText().trim();
        String email = emailTF.getText().trim();
        String address = addressTF.getText().trim();

        // Build confirmation message
        StringBuilder roomDetails = new StringBuilder();
        for (Room room : selectedRooms) {
            roomDetails.append(room.getRoomID()).append(" (").append(room.getRoomType()).append("), ");
        }
        String roomsList = roomDetails.length() > 0 ? roomDetails.substring(0, roomDetails.length() - 2) : "None";

        double totalPayable = amount + tax;

        boolean confirm = AlertHelper.showConfirmationAlert("Reservation details",
                "Guest Name: " + name + "\n" +
                        "Phone Number: " + phoneNumber + "\n" +
                        "Email: " + email + "\n" +
                        "Address: " + address + "\n" + "\n" +
                        "Number of Guests: " + numGuests + "\n" +
                        "Check In: " + checkIn + "\n" +
                        "Check Out: " + checkOut + "\n" + "\n" +
                        "Selected Rooms: " + roomsList + "\n" + "\n" +
                        "Total Amount: " + amount + "\n" +
                        "Tax: " + tax + "\n" +
                        "Total Payable: " + totalPayable
        );

        if (!confirm) {
            logger.info("User canceled reservation confirmation.");
            return; // Stop if user cancels
        }

        Guest guest = new Guest(name, phoneNumber, email, address);
        Reservation reservation = new Reservation(guest.getGuestID(), selectedRooms,
                checkIn, checkOut, numGuests, "CONFIRMED");
        Billing billing = new Billing(reservation.getReservationID(), amount, tax, 0);

        Database db = Database.getInstance();
        db.saveGuest(guest);
        db.saveReservation(reservation);
        db.saveBilling(billing);

        logger.info("Reservation created: " + reservation.getReservationID());
        AlertHelper.showInformationAlert("Reservation created", "Booking is confirmed!");
        getStage().setTitle("Admin Dashboard");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));

        AdminBookingOneReservationDetails adminBookingOneReservationDetails = (AdminBookingOneReservationDetails) HotelReservationApplication.getControllers().get(SceneName.Admin_Booking_ReservationDetails);
        adminBookingOneReservationDetails.resetFields();

        AdminBookinTwoRoomSelection adminBookinTwoRoomSelection = (AdminBookinTwoRoomSelection) HotelReservationApplication.getControllers().get(SceneName.Admin_Booking_RoomSelection);
        adminBookinTwoRoomSelection.resetFields();

        AdminBooklingThreeGuestDetails adminBooklingThreeGuestDetails = (AdminBooklingThreeGuestDetails) HotelReservationApplication.getControllers().get(SceneName.Admin_Booking_GuestDetails);
        adminBooklingThreeGuestDetails.resetFields();

    }

    public Stage getStage() {
        return (Stage) nameTF.getScene().getWindow();
    }


    public void resetFields() {
        // Reset text fields
        nameTF.setText("");
        phoneTF.setText("");
        emailTF.setText("");
        addressTF.setText("");

        // Reset instance variables
        checkIn = null;
        checkOut = null;
        numGuests = 0;
        selectedRooms = null;
        amount = 0.0;

    }


}
