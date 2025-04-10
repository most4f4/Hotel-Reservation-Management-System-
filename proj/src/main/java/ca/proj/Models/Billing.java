package ca.proj.Models;

import java.util.UUID;

public class Billing {
    private String billID;
    private String reservationID;
    private double amount;
    private double tax;
    private double discount;

    public Billing(String reservationID, double amount, double tax, double discount) {
        this.billID = UUID.randomUUID().toString();
        this.reservationID = reservationID;
        this.amount = amount;
        this.tax = tax;
        this.discount = discount;
    }

    // Getters
    public String getBillID() { return billID; }
    public String getReservationID() { return reservationID; }
    public double getAmount() { return amount; }
    public double getDiscount() { return discount; }
    public double getTax() {return tax;}
    public double getTotal() { return (amount + tax) * (1 - (discount/100));}

    @Override
    public String toString() {
        return "Billing{" +
                "billID='" + billID + '\'' +
                ", reservationID='" + reservationID + '\'' +
                ", amount=" + amount +
                ", tax=" + tax +
                ", discount=" + discount +
                '}';
    }
}
