package ca.proj.Models;

import ca.proj.Utility.RoomType;

public class Room {
    private String roomID;
    private RoomType roomType;
    private int numberOfBeds;
    private double price;

    public Room(String roomID, RoomType roomType, double price) {
        this.roomID = roomID;
        this.roomType = roomType;
        setNumberOfBeds();
        this.price = price;
    }

    // Getters
    public String getRoomID() { return roomID; }
    public RoomType getRoomType() { return roomType; }
    public int getNumberOfBeds() { return numberOfBeds; }
    public double getPrice() { return price; }


    private void setNumberOfBeds() {
        switch (roomType) {
            case SINGLE: case DELUXE: case PENTHOUSE: this.numberOfBeds = 1; break;
            case DOUBLE: this.numberOfBeds = 2; break;
        }
    }

    @Override
    public String toString() {
        return "Room " + getRoomID() + " (" + getRoomType() + ") ";
    }
}
