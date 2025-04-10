package ca.proj.Controllers;

import ca.proj.HotelReservationApplication;
import ca.proj.Utility.SceneName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class WelcomeController {


    private MediaPlayer mediaPlayer;
    @FXML private MediaView mediaView;

    public void initialize() {
        String videoPath = new File("src/main/resources/Assets/welcome.mp4").toURI().toString();
        Media media = new Media(videoPath);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // Auto-play video
        mediaPlayer.setAutoPlay(true);

        // Stop video when it ends
        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("Video ended. Load next scene if needed.");
        });

    }

    @FXML void loginPressed(ActionEvent event) {
        mediaPlayer.stop();
        getStage().setTitle("Admin Login");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Login));
    }

    @FXML void feedbackButtonPressed(ActionEvent event) {
        mediaPlayer.stop();
        getStage().setTitle("Feedback");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.Feedback));
    }

    @FXML void nextButtonPressed(ActionEvent event) {
        mediaPlayer.stop();
        getStage().setTitle("Reservation Details");
        getStage().setScene(HotelReservationApplication.getScenes().get(SceneName.ReservationDetails));

    }

    @FXML void replayButtonPressed(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getStartTime());
        mediaPlayer.play();
    }

    public Stage getStage() {
        return (Stage) mediaView.getScene().getWindow();
    }

}
