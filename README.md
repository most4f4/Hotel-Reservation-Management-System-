# Hotel Reservation Management System

## 📌 Project Overview

This **Hotel Reservation Management System** is a JavaFX desktop application designed to simplify hotel operations including room booking, guest data management, and administrative oversight. It provides a user-friendly interface for managing reservations, guest feedback, billing, and room availability. The system includes both client-side and server-side components, with an admin interface for managing hotel operations and a guest interface for submitting feedback.
This project was developed as part of a university course to demonstrate proficiency in Java, JavaFX, database management, and client-server architecture.

## ✨ Features

### 👤 Guest Side

- View available rooms.
- Enter reservation and personal details.
- Choose a room and confirm the reservation.
- Submit feedback after the stay.

### 🔐 Admin Side

- Secure login for admin.
- Check available rooms.
- View and manage current bookings (Check-In, Check-Out, Cancel, Delete Reservations).
- Apply discounts to reservations.
- View guest feedback with ratings.
- View guest information linked to bookings.
- Search for guests by name.
- Real-time multi-threaded admin access (via socket-based client-server module).
- Generate billing summaries with tax and discount calculations.

### 💯 Client-Server Architecture\*\*:

- Admin client connects to a server running on port 12345 for secure operations.

### 🔑 Database Integration\*\*:

- SQLite database for storing guests, reservations, rooms, feedback, and billing data.

### 🎯 Logging\*\*:

- Comprehensive logging for debugging and monitoring using `LoggerUtil`.

## Technologies Used

- **Java**: Core programming language (Java 17).
- **JavaFX**: For building the GUI (JavaFX 17).
- **SQLite**: Lightweight database for data persistence.
- **JDBC**: For database connectivity.
- **Maven**: Dependency management and build tool.
- **Scene Builder**: For designing FXML-based UI layouts.
- **IntelliJ IDEA**: Recommended IDE for development.

## 📁 Project Structure

```
Hotel-Reservation-Management-System/
├── src/
│   └── ca/proj/
│       ├── Controllers/       # JavaFX Controllers
│       ├── Database/          # DB connection and queries
│       ├── Models/            # Core models like Reservation, Room, Guest
│       ├── ServerClient/      # AdminClient and AdminServer classes
│       ├── Utility/           # Logger and helpers
│       └── Views/             # FXML views for all screens
├── resources/                 # Icons and additional assets
├── README.md
```

## 🚀 Getting Started

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** (or later): [Download JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Maven**: [Download Maven](https://maven.apache.org/download.cgi)
- **SQLite**: No installation needed (embedded in the project).
- **JavaFX 17**: Included via Maven dependencies.
- **Scene Builder** (optional, for editing FXML files): [Download Scene Builder 17](https://gluonhq.com/products/scene-builder/#download)

### Running the Application

1. Open the project in IntelliJ IDEA.
2. Build the project and ensure dependencies are set.
3. Run `HotelReservationApplication.java` to launch the UI.
4. To start admin server access, run `AdminServer.java`.
5. To connect as an admin, run `AdminClient.java` in a separate process.

## 🧪 Usage Instructions

### User Flow

- Start on the Welcome Screen.
- Enter reservation details.
- Select a room.
- Provide guest details.
- Confirm the booking and receive a summary.
- Optionally leave feedback.

### Admin Flow

- Start on the Welcome Screen.
- Accesss to Login Page using Menu.
- Login using username and password.
- Access Admin Dashboard.
- - Add a Booking.
- - View Current Bookings.
- - Available Rooms.
- - Search in History of Bookings.
- - Access Bill Services.
- - View Reviews.

### Multi-threaded Server Client Coomunication for Multiple Admins

- Login using username and password.
- Use terminal commands to search guest info or manage reservations.
- Multiple admins can connect simultaneously via TCP server.

## 💾 Database Schema Overview

- `guests(guest_id, name, email, phone, address)`
- `reservations(reservation_id, guest_id, check_in, check_out, number_of_guests, status)`
- `rooms(room_id, type, number_of_beds, price)`
- `reservation_rooms(reservation_id, room_id)`
- `billing(bill_id, reservation_id, amount, tax, discount)`
- `feedback(feedback_id, reservation_id, rating, comment)`
- `admins(admin_id, username, password)`

## 📸 Screenshots

> _(Add screenshots of your UI here in the GitHub repo once pushed.)_

## 📚 Credits

This project was implemented as part of a desktop development course/project. It showcases complete full-stack logic using JavaFX and JDBC with a clean MVC separation.

## 📃 License

MIT License – free to use with attribution.
