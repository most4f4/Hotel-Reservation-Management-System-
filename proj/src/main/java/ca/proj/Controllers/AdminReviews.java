package ca.proj.Controllers;

import ca.proj.Database.Database;
import ca.proj.HotelReservationApplication;
import ca.proj.Models.Feedback;
import ca.proj.Models.Reservation;
import ca.proj.Utility.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AdminReviews implements Initializable {

    @FXML private TableView<Feedback> feedbacksTV;
    @FXML private TableColumn<Feedback, Double> ratingTC;
    @FXML private TableColumn<Feedback, String> guestNameTC;
    @FXML private TableColumn<Feedback, LocalDate> checkInDate;
    @FXML private TableColumn<Feedback, Integer> numNightsTC;
    @FXML private TableColumn<Feedback, String> roomsTC;
    @FXML private TableColumn<Feedback, String> feedbackTC;
    @FXML private Label ratingLabel;
    private Database db;
    private static final Logger logger = LoggerUtil.getLogger();

    @Override public void initialize(URL url, ResourceBundle resourceBundle){
        this.db = Database.getInstance();
        double average = calculateAverageRating(db);
        ratingLabel.setText(String.format("%.2f", average));

        ratingTC.setCellValueFactory(new PropertyValueFactory<>("rating"));
        feedbackTC.setCellValueFactory(new PropertyValueFactory<>("comment"));
        guestNameTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuestName(db)));
        checkInDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getCheckInDate(db);
            return new SimpleObjectProperty<>(date); // Return LocalDate directly
        });
        numNightsTC.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNumNights(db)).asObject());
        roomsTC.setCellValueFactory(data-> new SimpleStringProperty(data.getValue().getRooms(db)));

        TableColumnModifier.centerAlignColumn(ratingTC);
        TableColumnModifier.centerAlignColumn(guestNameTC);
        TableColumnModifier.centerAlignColumn(checkInDate);
        TableColumnModifier.centerAlignColumn(roomsTC);
        TableColumnModifier.centerAlignColumn(numNightsTC);
        TableColumnModifier.centerAlignColumn(feedbackTC);

        feedbacksTV.setOnMouseClicked(e->{
            if (e.getClickCount() == 2){
                handleDoubleClick(e);
            }
        });

        loadReviews();
    }

    private void loadReviews() {
        // Set average rating
        double avgRating = db.getAverageFeedbackRating();
        ratingLabel.setText(String.format("%.2f", avgRating));

        // Populate table
        ObservableList<Feedback> feedbackList = FXCollections.observableArrayList(db.getAllFeedback());
        feedbacksTV.setItems(feedbackList);
    }

    private double calculateAverageRating(Database db) {
        return db.getAverageFeedbackRating();
    }

    @FXML void previousBtnPressed(ActionEvent event) {
        getStage().setTitle("Admin Dashboard");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.AdminDashboard));
    }

    public void handleDoubleClick(MouseEvent e){
        Feedback feedback = feedbacksTV.getSelectionModel().getSelectedItem();
        if (feedback != null) {
            showReservationDetailsDialog(feedback);
        }
    }

    private void showReservationDetailsDialog(Feedback feedback) {
        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Feedback Details");
        dialog.setHeaderText("Details for Reservation ID: " + feedback.getReservationID());

        // Set button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        // Create grid for details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        // Add reservation details
        grid.add(new Label("Reservation ID:"), 0, 0);
        grid.add(new Label(feedback.getReservationID()), 1, 0);
        grid.add(new Label("Guest Name:"), 0, 1);
        grid.add(new Label(feedback.getGuestName(db)), 1, 1);
        grid.add(new Label("Rooms:"), 0, 2);
        grid.add(new Label(feedback.getRooms(db)), 1, 2);
        grid.add(new Label("Check-In Date:"), 0, 3);
        grid.add(new Label(feedback.getCheckInDate(db).toString()), 1, 3);
        grid.add(new Label("Number of Nights:"), 0, 4);
        grid.add(new Label(String.valueOf(feedback.getNumNights(db))), 1, 4);
        grid.add(new Label("Rooms:"), 0, 5);
        grid.add(new Label(String.valueOf(feedback.getRooms(db))), 1, 5);
        grid.add(new Label("Feedback:"), 0, 6);
        grid.add(new Label(feedback.getComment()), 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Handle result
        dialog.showAndWait();

    }



    @FXML void rulesBtnPressed(ActionEvent event) {
        Rules.rulesButtonPressed();
    }

    @FXML void signOutBtnPressed(ActionEvent event) {WindlowLoader.signOutProcess(getStage());}

    public Stage getStage() {
        return (Stage) ratingLabel.getScene().getWindow();
    }

}
