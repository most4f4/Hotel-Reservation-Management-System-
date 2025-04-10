package ca.proj.Models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Guest {
    private String guestID;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String feedback;

    public Guest(){
        this("","","","");
    }

    // Constructor
    public Guest(String name, String phoneNumber, String email, String address) {
        this.guestID = UUID.randomUUID().toString();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Getters
    public String getGuestID() { return guestID; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getFeedback() { return feedback; }

    // Setters
    public void setFeedback(String feedback) { this.feedback = feedback; }

    @Override
    public String toString() {
        return "Guest{" +
                "guestID='" + guestID + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }


}
