package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Models.Reservation;
import ca.proj.Utility.Rules;
import ca.proj.Utility.SceneName;
import ca.proj.Utility.TableColumnModifier;
import ca.proj.Utility.WindlowLoader;
import ca.proj.Database.Database;
import javafx.beans.property.SimpleDoubleProperty;
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

public class AdminSearchController implements Initializable {

    @FXML private TableView<Reservation> allBookingsTV;
    @FXML private TableColumn<Reservation, String> reservationIDTC;
    @FXML private TableColumn<Reservation, String> guestNameTC;
    @FXML private TableColumn<Reservation, LocalDate> checkInDate;
    @FXML private TableColumn<Reservation, LocalDate> checkOutDate;
    @FXML private TableColumn<Reservation, Double> priceTC;
    @FXML private TableColumn<Reservation, String> statusTC;
    @FXML private TableColumn<Reservation, String> roomsTC;
    @FXML private TextField searchTF;
    ObservableList<Reservation> allbookings;
    private Database db;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        db = Database.getInstance();

        // Set up TableView columns
        reservationIDTC.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        guestNameTC.setCellValueFactory(cellData->{
            return new SimpleStringProperty(cellData.getValue().getGuestName(db));
        });
        checkInDate.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutDate.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        priceTC.setCellValueFactory(cellData ->{
            return new SimpleDoubleProperty(cellData.getValue().getTotalPrice(db)).asObject();
        });
        statusTC.setCellValueFactory(new PropertyValueFactory<>("status"));
        roomsTC.setCellValueFactory(data->{
            return new SimpleStringProperty(data.getValue().getRoomsString());
        });

        TableColumnModifier.centerAlignColumn(reservationIDTC);
        TableColumnModifier.centerAlignColumn(guestNameTC);
        TableColumnModifier.centerAlignColumn(checkInDate);
        TableColumnModifier.centerAlignColumn(checkOutDate);
        TableColumnModifier.centerAlignColumn(priceTC);
        TableColumnModifier.centerAlignColumn(statusTC);
        TableColumnModifier.centerAlignColumn(roomsTC);

        allBookingsTV.setOnMouseClicked(e->{
            if (e.getClickCount() == 2){
                handleDoubleClick(e);
            }
        });

        // Load initial data
        loadBookings();

        // Search functionality
        searchTF.textProperty().addListener((obs, oldValue, newValue) -> filterBookings(newValue.trim()));
    }

    private void loadBookings(){
        allbookings = FXCollections.observableArrayList(db.getAllBookings());
        allBookingsTV.setItems(allbookings);
    }

    private void filterBookings(String searchText){
        if (searchText.isEmpty()){
            allBookingsTV.setItems(allbookings);
        } else {
            ObservableList<Reservation> filtered = FXCollections.observableArrayList();
            for (Reservation res: allbookings){
                if (res.getGuestName(db).toLowerCase().contains(searchText.toLowerCase())){
                    filtered.add(res);
                }
            }
            allBookingsTV.setItems(filtered);
        }
    }

    public void handleDoubleClick(MouseEvent e){
        Reservation res = allBookingsTV.getSelectionModel().getSelectedItem();
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

    @FXML void rulesBtnPressed(ActionEvent event) {Rules.rulesButtonPressed();}

    @FXML void sigoutBtnPressed(ActionEvent event) {
        resetFields();
        WindlowLoader.signOutProcess(getStage());}

    private Stage getStage(){return (Stage) searchTF.getScene().getWindow();}

    public void resetFields() {
        searchTF.setText("");
    }

}
