package ca.proj.Utility;

public class Rules {
    public static void rulesButtonPressed(){
        AlertHelper.showInformationAlert("Rules and Regulations",
                "Reservation Rules\n" +
                        "- Room Capacities: Single, Deluxe, and Penthouse rooms accommodate up to 2 guests each. Double rooms accommodate up to 4 guests.\n" +
                        "- Minimum Rooms: For 1-2 guests: 1 room (Single/Deluxe/Penthouse); 3-4 guests: 1 Double; 5-6 guests: 1 Double + 1 other; 7-8 guests: 2 Doubles. You may select additional rooms if desired.\n" +
                        "- Selection: Choose rooms one at a time using the '>>' button. Use '<<' to adjust your selection.\n" +
                        "- Capacity Requirement: Total room capacity must meet or exceed the number of guests.\n" +
                        "- Availability: Rooms are subject to availability for your selected dates.\n" +
                        "- Confirmation: Complete guest details to finalize your reservation.");
    }
}
