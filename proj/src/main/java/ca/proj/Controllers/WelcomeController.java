package ca.proj.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class WelcomeController {


    private MediaPlayer mediaPlayer;

    @FXML private MediaView mediaView;
    @FXML private ImageView feedbackImage;
    @FXML private ImageView nextImage;
    @FXML private ImageView replayImage;

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

        nextImage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            mediaPlayer.stop();
            System.out.println("Skipped! Load next scene here.");
            // Code to switch scenes if needed
        });

        replayImage.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->{
            mediaPlayer.seek(mediaPlayer.getStartTime());
            mediaPlayer.play();
        });

    }


    @FXML void closePressed(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getStartTime());
        mediaPlayer.play();
    }

    @FXML void loginPressed(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getStartTime());
        mediaPlayer.play();
    }

}
