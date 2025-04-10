package ca.proj.Models;

import ca.proj.Database.Database;

import java.time.LocalDate;
import java.util.UUID;

public class Feedback {
    private String feedbackID;
    private String reservationID;
    private String comment;
    private int rating;

    public Feedback(String reservationID, int rating, String comments) {
        this.feedbackID = UUID.randomUUID().toString();;
        this.reservationID = reservationID;
        this.comment = comments;
        this.rating = rating;
    }

    // Getters
    public String getFeedbackID() { return feedbackID; }
    public String getReservationID() { return reservationID; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }

    public String getGuestName(Database db){
        return db.getGuestNameByReservationID(reservationID);
    }

    public LocalDate getCheckInDate(Database db){
        return db.getCheckInDateByReservationID(reservationID);
    }

    public int getNumNights(Database db){
        return db.getNumberNightsByReservationID(reservationID);
    }

    public String getRooms(Database db) {
        return db.getRooms(reservationID);
    }


}
