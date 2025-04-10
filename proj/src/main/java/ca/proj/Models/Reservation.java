package ca.proj.Models;

import ca.proj.Database.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reservation {
    private String reservationID;
    private String guestID;
    private List<Room> rooms;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private String status; // CONFIRMED, CHECKED-IN, CHECKED-OUT, CANCELLED

    public Reservation(String guestID, List<Room> rooms,
                       LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests, String status) {
        this.reservationID = UUID.randomUUID().toString();
        this.guestID = guestID;
        this.rooms = rooms != null ? new ArrayList<>(rooms) : new ArrayList<>();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
    }

    public Reservation(String reservationID, String guestID, List<Room> rooms,
                       LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests, String status) {
        this.reservationID = String.valueOf(reservationID);
        this.guestID = guestID;
        this.rooms = rooms != null ? new ArrayList<>(rooms) : new ArrayList<>();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
    }


    // Getters and Setters
    public String getReservationID() { return reservationID; }
    public String getGuestID() { return guestID; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); } // Defensive copy
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public int getNumberOfGuests() {return numberOfGuests;}
    public String getStatus() {return status;}
    public void setRooms(List<Room> rooms) { this.rooms = new ArrayList<>(rooms); }

    // For TableView (computed properties)
    public String getGuestName(Database db) {
        String guestName = db.getGuestById(guestID);
        return guestName != null ? guestName : "Unknown";
    }

    public String getRoomsString() {
        StringBuilder roomDetails = new StringBuilder();
        for (Room room : rooms) {
            roomDetails.append(room.getRoomID()).append(" (").append(room.getRoomType()).append("), ");
        }
        return roomDetails.length() > 0 ? roomDetails.substring(0, roomDetails.length() - 2) : "None";
    }

    public Integer getNumNights() {
        int nights = (int) (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
        return nights >= 0 ? nights : 0; // Ensure non-negative
    }

    public Double getTotalPrice(Database db) {
        Double payable = db.getPayableAmount(reservationID);
        return payable;
    }


}
