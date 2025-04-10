package ca.proj;

import ca.proj.Utility.SceneName;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HotelReservationApplication extends Application {

    private static Map<SceneName, Scene> scenes = new HashMap<>();
    private static Map<SceneName, Object> controllers = new HashMap<>();

    @Override public void start(Stage stage) throws IOException {

        scenes.put(SceneName.Welcome, loadScene("Views/welcome-view.fxml", SceneName.Welcome));
        scenes.put(SceneName.ReservationDetails, loadScene("Views/getInfo-view.fxml", SceneName.ReservationDetails));
        scenes.put(SceneName.RoomSelection, loadScene("Views/roomSelection-view.fxml", SceneName.RoomSelection));
        scenes.put(SceneName.GuestDetails, loadScene("Views/personalDetails-view.fxml", SceneName.GuestDetails));
        scenes.put(SceneName.ConfirmationRes, loadScene("Views/reserveConfirmation-view.fxml", SceneName.ConfirmationRes));
        scenes.put(SceneName.Feedback, loadScene("Views/feedback-view.fxml", SceneName.Feedback));
        scenes.put(SceneName.Login, loadScene("Views/admin-login.fxml", SceneName.Login));
        scenes.put(SceneName.AdminDashboard, loadScene("Views/admin-homepage.fxml", SceneName.AdminDashboard));
        scenes.put(SceneName.Admin_Booking_ReservationDetails, loadScene("Views/admin-booking-one.fxml", SceneName.Admin_Booking_ReservationDetails));
        scenes.put(SceneName.Admin_Booking_RoomSelection, loadScene("Views/admin-booking-two.fxml", SceneName.Admin_Booking_RoomSelection));
        scenes.put(SceneName.Admin_Booking_GuestDetails, loadScene("Views/admin-booking-three.fxml", SceneName.Admin_Booking_GuestDetails));

        stage.setTitle("Welcome!");
        stage.setScene(scenes.get(SceneName.Welcome));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private Scene loadScene(String fxmlFile, SceneName sceneName){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            controllers.put(sceneName, loader.getController());
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<SceneName, Scene> getScenes() {
        return scenes;
    }

    public static Map<SceneName, Object> getControllers() {
        return controllers;
    }

}