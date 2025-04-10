package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Utility.*;
import ca.proj.Models.Room;
import ca.proj.Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AdminAvailableRoomsController implements Initializable {

    @FXML private TableView<Room> availableRoomsTV;
    @FXML private TableColumn<Room, String> roomIDTC;
    @FXML private TableColumn<Room, Double> roomPriceTC;
    @FXML private TableColumn<Room, RoomType> roomTypeTC;
    private Database database;

    @Override public void initialize (URL url, ResourceBundle resourceBundle) {
        this.database = Database.getInstance();
        roomIDTC.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        roomTypeTC.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomPriceTC.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumnModifier.centerAlignColumn(roomIDTC);
        TableColumnModifier.centerAlignColumn(roomTypeTC);
        TableColumnModifier.centerAlignColumn(roomPriceTC);

        loadAvailableRooms();
    }

    private void loadAvailableRooms(){
        List<Room> rooms = database.getAvailableRoomsForDates(LocalDate.now(), LocalDate.now());
        availableRoomsTV.getItems().clear();
        availableRoomsTV.getItems().addAll(rooms);
    }

    @FXML void addBookingPressed(ActionEvent event) {
        getStage().setTitle("Reservation Details");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Admin_Booking_ReservationDetails));
    }

    @FXML void previousBtnPressed(ActionEvent event) {
        getStage().setTitle("Admin Dashboard");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));
    }

    @FXML void rulesBtnPressed(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

    @FXML void signOutBtnpressed(ActionEvent event) {
        WindlowLoader.signOutProcess(getStage());
    }

    public Stage getStage() {
        return (Stage) availableRoomsTV.getScene().getWindow();
    }

}
