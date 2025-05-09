package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Models.Billing;
import ca.proj.Models.Reservation;
import ca.proj.Utility.*;
import ca.proj.Database.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AdminBillingService implements Initializable {

    @FXML private TableColumn<Reservation, String> roomsTC;
    @FXML private TableColumn<Reservation, Double> amountTC;
    @FXML private TableColumn<Reservation, LocalDate> checkOutTC;
    @FXML private TableColumn<Reservation, String> guestNameTC;
    @FXML private TableColumn<Reservation, String> reservationIDTC;
    @FXML private TextField searchTF;
    @FXML private TableView<Reservation> currentBookingsTV;

    private ObservableList<Reservation> allBookings;
    private static final Logger logger = LoggerUtil.getLogger();
    private Database db;

    @Override public void initialize(URL url, ResourceBundle resourceBundle){
        db = Database.getInstance();

        reservationIDTC.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        checkOutTC.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        guestNameTC.setCellValueFactory(cellData ->{
            return new SimpleStringProperty(cellData.getValue().getGuestName(db));
        });
        roomsTC.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getRoomsString());
        });
        amountTC.setCellValueFactory(cellData -> {
            return new SimpleDoubleProperty(cellData.getValue().getTotalPrice(db)).asObject();
        });

        TableColumnModifier.centerAlignColumn(reservationIDTC);
        TableColumnModifier.centerAlignColumn(guestNameTC);
        TableColumnModifier.centerAlignColumn(checkOutTC);
        TableColumnModifier.centerAlignColumn(roomsTC);

        amountTC.setCellFactory(column -> new TableCell<Reservation, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item)); // Format to 2 decimal places
                    setAlignment(Pos.CENTER);
                }
            }
        });

        loadBookings();

        // Search functionality
        searchTF.textProperty().addListener((obs, oldValue, newValue) -> filterBookings(newValue.trim()));

        currentBookingsTV.setOnMouseClicked(e ->{
            if (e.getClickCount() == 2){
                handleDoubleClick(e);
            }
        });
    }

    private void loadBookings(){
        allBookings = FXCollections.observableArrayList(db.getCurrentBookings());
        currentBookingsTV.setItems(allBookings);
    }

    private void filterBookings(String nameOrPhone) {
        if (nameOrPhone.isEmpty()) {
            currentBookingsTV.setItems(allBookings);
        } else {
            ObservableList<Reservation> filtered = FXCollections.observableArrayList();
            for(Reservation res : allBookings){
                if (res.getGuestName(db).toLowerCase().contains(nameOrPhone.toLowerCase())){
                    filtered.add(res);
                }
            }
            currentBookingsTV.setItems(filtered);
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

    @FXML void signoutBtnPressed(ActionEvent event) {
        resetFields();
        WindlowLoader.signOutProcess(getStage());
    }

    public Stage getStage(){
        return (Stage) searchTF.getScene().getWindow();
    }

    public void resetFields() {
        searchTF.setText("");
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
        dialog.setTitle("Reservation Details");
        dialog.setHeaderText("Details for Reservation ID: " + reservation.getReservationID());

        // Set button types
        ButtonType applyButtonType = new ButtonType("Apply Discount", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

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
        grid.add(new Label("Check-Out Date:"), 0, 4);
        grid.add(new Label(reservation.getCheckOutDate().toString()), 1, 4);
        grid.add(new Label("Number of Guests:"), 0, 5);
        grid.add(new Label(String.valueOf(reservation.getNumberOfGuests())), 1, 5);
        grid.add(new Label("Current Amount:"), 0, 6);
        Label currentAmountLabel = new Label(String.format("$%.2f", reservation.getTotalPrice(db)));
        grid.add(currentAmountLabel, 1, 6);

        // Add discount combo box
        grid.add(new Label("Apply Discount:"), 0, 7);
        ComboBox<Double> discountCombo = new ComboBox<>();
        discountCombo.getItems().addAll(0.0, 5.0, 10.0, 15.0, 20.0); // Discount options in dollars
        discountCombo.setValue(0.0); // Default no discount
        grid.add(discountCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);

        // Handle result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == applyButtonType) {
            double discount = discountCombo.getValue();
            if (discount > 0.0) {
                db.applyDiscount(reservation, discount);
                currentAmountLabel.setText(String.format("$%.2f", reservation.getTotalPrice(db)));
                currentBookingsTV.refresh();
            }
        }
    }

    @FXML void checkOutBtnPressed(ActionEvent event) {
        Reservation selected = currentBookingsTV.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showErrorAlert("Error", "Please select a booking to check out.");
            return;
        }
        boolean confirmed = AlertHelper.showConfirmationAlert("Confirm Check Out",
                "Are you sure you want to check out " + selected.getGuestName(db) + "?");
        if (confirmed) {
            if (db.checkOut(selected.getReservationID())) {
                loadBookings();
                AlertHelper.showInformationAlert("Success", "Guest checked out successfully.");
            } else {
                AlertHelper.showErrorAlert("Error", "Failed to check out guest.");

            }
        }
    }


}
