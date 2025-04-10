package ca.proj.Database;

import ca.proj.Models.*;
import ca.proj.Utility.LoggerUtil;
import ca.proj.Utility.RoomType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.DAYS;

public class Database {
    private static final String DB_NAME = "database.db";
    private static final String DB_JDBC = "jdbc:sqlite:";
    private static final String DB_CONNECTION = "C:\\Users\\mosta\\OneDrive\\Desktop\\Semester5\\APD545\\Hotel-Reservation-Management-System-\\proj\\src\\main\\java\\ca\\proj\\database\\";
    private static final Logger logger = LoggerUtil.getLogger(); // Use shared logger
    private static final Database instance = new Database();

    private Database() {
        initializeDatabase();
    }

    public static Database getInstance() {
        return instance;
    }

    private void initializeDatabase() {
        //dropTables();
        createTables();
        preloadRooms();
    }

    private void dropTables() {
        String[] dropAllTables = {
                "DROP TABLE IF EXISTS reservation_rooms",
                "DROP TABLE IF EXISTS billing",
                "DROP TABLE IF EXISTS feedback",
                "DROP TABLE IF EXISTS reservations",
                "DROP TABLE IF EXISTS rooms",
                "DROP TABLE IF EXISTS guests",
                "DROP TABLE IF EXISTS admins"
        };

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             Statement stmt = conn.createStatement()) {
            for (String sql : dropAllTables) {
                stmt.execute(sql);
            }
            logger.info("Database tables dropped successfully");
        } catch (SQLException e) {
            logger.severe("Error dropping tables: " + e.getMessage());
        }
    }

    private void createTables() {
        String[] createTableQueries = {
                "CREATE TABLE IF NOT EXISTS guests (" +
                        "guest_id TEXT PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "phone_number TEXT NOT NULL UNIQUE, " +
                        "email TEXT NOT NULL, " +
                        "address TEXT)",

                "CREATE TABLE IF NOT EXISTS reservations (" +
                        "reservation_id TEXT PRIMARY KEY, " +
                        "guest_id TEXT, " +
                        "check_in_date TEXT, " +
                        "check_out_date TEXT, " +
                        "number_of_guests INTEGER, " +
                        "status TEXT, " +
                        "FOREIGN KEY (guest_id) REFERENCES guests(guest_id))",

                "CREATE TABLE IF NOT EXISTS rooms (" +
                        "room_id TEXT PRIMARY KEY, " +
                        "room_type TEXT, " +
                        "number_of_beds INTEGER, " +
                        "price REAL)" ,

                "CREATE TABLE IF NOT EXISTS reservation_rooms (" +
                        "reservation_id TEXT, " +
                        "room_id TEXT, " +
                        "PRIMARY KEY (reservation_id, room_id), " +
                        "FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id), " +
                        "FOREIGN KEY (room_id) REFERENCES rooms(room_id))",

                "CREATE TABLE IF NOT EXISTS billing (" +
                        "bill_id TEXT PRIMARY KEY, " +
                        "reservation_id TEXT, " +
                        "amount REAL, " +
                        "tax REAL, " +
                        "discount REAL, " +
                        "FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id))",

                "CREATE TABLE IF NOT EXISTS feedback (" +
                        "feedback_id TEXT PRIMARY KEY, " +
                        "reservation_id TEXT, " +
                        "rating INTEGER, " +
                        "comment TEXT, " +
                        "FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id))",

                "CREATE TABLE IF NOT EXISTS admins (" +
                        "admin_id TEXT PRIMARY KEY, " +
                        "username TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL)"
        };


        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             Statement stmt = conn.createStatement()) {
            for (String query : createTableQueries) {
                stmt.execute(query);
            }
            logger.info("Database tables created successfully");

        } catch (SQLException e) {
            logger.severe("Error creating or dropping tables: " + e.getMessage());
        }

        // Preload admins (unchanged)
        String insertAdmins = "INSERT OR IGNORE INTO admins (admin_id, username, password) VALUES " +
                "('1', 'admin1', 'password123'), " +
                "('2', 'admin2', 'password456')";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             Statement stmt = conn.createStatement()) {
            stmt.execute(insertAdmins);
            logger.info("Admins preloaded");
        } catch (SQLException e) {
            logger.severe("Error preloading admins: " + e.getMessage());
        }
    }

    private void preloadRooms() {
        String roomSql = "INSERT OR IGNORE INTO rooms (room_id, room_type, number_of_beds, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME)) {
            conn.setAutoCommit(false); // Start transaction

            Room[] initialRooms = {
                    new Room("101", RoomType.SINGLE, 100.0),
                    new Room("102", RoomType.SINGLE, 100.0),
                    new Room("103", RoomType.SINGLE, 100.0),
                    new Room("104", RoomType.SINGLE, 100.0),
                    new Room("105", RoomType.SINGLE, 100.0),
                    new Room("201", RoomType.DOUBLE, 150.0),
                    new Room("202", RoomType.DOUBLE, 150.0),
                    new Room("203", RoomType.DOUBLE, 150.0),
                    new Room("204", RoomType.DOUBLE, 150.0),
                    new Room("205", RoomType.DOUBLE, 150.0),
                    new Room("301", RoomType.DELUXE, 200.0),
                    new Room("302", RoomType.DELUXE, 200.0),
                    new Room("401", RoomType.PENTHOUSE, 300.0)
            };

            try (PreparedStatement pstmt = conn.prepareStatement(roomSql)) {
                for (Room room : initialRooms) {
                    pstmt.setString(1, room.getRoomID());
                    pstmt.setString(2, room.getRoomType().toString());
                    pstmt.setInt(3, room.getNumberOfBeds());
                    pstmt.setDouble(4, room.getPrice());
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            logger.info("Rooms preloaded successfully");
        } catch (SQLException e) {
            logger.severe("Error preloading rooms: " + e.getMessage());
        }
    }

    public static List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT r.* FROM rooms r " +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 FROM reservation_rooms rr " +
                "    JOIN reservations res ON rr.reservation_id = res.reservation_id " +
                "    WHERE rr.room_id = r.room_id " +
                "    AND (res.check_in_date <= ? AND res.check_out_date >= ?)" +
                "    AND res.status NOT IN ('CANCELED', 'CHECKED-OUT')" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, checkOut.toString());
            pstmt.setString(2, checkIn.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Room room = new Room(rs.getString("room_id"),
                        RoomType.valueOf(rs.getString("room_type")),
                        rs.getDouble("price"));
                availableRooms.add(room);
            }
            logger.info("Fetched " + availableRooms.size() + " available rooms for dates: " + checkIn + " to " + checkOut);
        } catch (SQLException e) {
            logger.severe("Error fetching available rooms for dates: " + e.getMessage());
        }
        return availableRooms;
    }

    public void saveGuest(Guest guest) {
        String sql = "INSERT INTO guests (guest_id, name, phone_number, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guest.getGuestID());
            pstmt.setString(2, guest.getName());
            pstmt.setString(3, guest.getPhoneNumber());
            pstmt.setString(4, guest.getEmail());
            pstmt.setString(5, guest.getAddress());
            pstmt.executeUpdate();
            logger.info("Guest saved: " + guest.getGuestID());
        } catch (SQLException e) {
            logger.severe("Error saving guest: " + e.getMessage());
        }
    }

    public void saveBilling(Billing billing) {
        String sql = "INSERT INTO billing (bill_id, reservation_id, amount, tax, discount) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,billing.getBillID());
            ps.setString(2,billing.getReservationID());
            ps.setDouble(3,billing.getAmount());
            ps.setDouble(4,billing.getTax());
            ps.setDouble(5,billing.getDiscount());
            ps.executeUpdate();
            logger.info("Billing saved: " + billing.getBillID());
        } catch (SQLException e) {
            logger.severe("Error saving billing: " + e.getMessage());
        }
    }

    public void saveReservation(Reservation reservation) {
        String insertReservationSQL = "INSERT INTO reservations (reservation_id, guest_id, check_in_date, check_out_date, number_of_guests, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String insertRoomSQL = "INSERT INTO reservation_rooms (reservation_id, room_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement pstmtRes = conn.prepareStatement(insertReservationSQL);
             PreparedStatement pstmtRoom = conn.prepareStatement(insertRoomSQL)) {

            // Insert into reservations table
            pstmtRes.setString(1, reservation.getReservationID());
            pstmtRes.setString(2, reservation.getGuestID());
            pstmtRes.setString(3, reservation.getCheckInDate().toString());
            pstmtRes.setString(4, reservation.getCheckOutDate().toString());
            pstmtRes.setInt(5, reservation.getNumberOfGuests());
            pstmtRes.setString(6, reservation.getStatus());
            pstmtRes.executeUpdate();

            // Insert each room into reservation_rooms table
            for (Room room : reservation.getRooms()) {
                pstmtRoom.setString(1, reservation.getReservationID());
                pstmtRoom.setString(2, room.getRoomID());
                pstmtRoom.executeUpdate();
            }

            logger.info("Reservation saved: " + reservation.getReservationID());
        } catch (SQLException e) {
            logger.severe("Error saving reservation: " + e.getMessage());
        }
    }

    public boolean saveFeedback(Feedback newFeedback) {
        // Step 1: Check if feedback already exists for this reservation
        String checkSql = "SELECT * FROM feedback WHERE reservation_id = ?";

        // Step 2: Insert feedback if verified (include guest_id)
        String insertSql = "INSERT INTO feedback (feedback_id, reservation_id, rating, comment) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME)) {
            // Check existing feedback
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, newFeedback.getReservationID());
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    logger.warning("Feedback already submitted for reservation " + newFeedback.getReservationID());
                    return false;
                }
            }

            // Insert feedback
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, newFeedback.getFeedbackID());
                psInsert.setString(2, newFeedback.getReservationID()); // For the subquery
                psInsert.setInt(3, newFeedback.getRating());
                psInsert.setString(4, newFeedback.getComment());
                psInsert.executeUpdate();
                logger.info("Feedback saved for reservation " + newFeedback.getReservationID());
                return true;
            }
        } catch (SQLException e) {
            logger.severe("Error saving feedback: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticateLogin(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("User " + username + " is authenticated");
                return true;
            }
            logger.info("User " + username + " not authenticated");
            return false;
        } catch (SQLException e) {
            logger.severe("Error logging in: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> getCurrentBookings() {
        List<Reservation> bookings = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM reservations " +
                "WHERE status NOT IN ('CHECKED-OUT', 'CANCELED')";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String reservationID = rs.getString("reservation_id");
                List<Room> rooms = getRoomsForReservation(reservationID); // Helper method
                Reservation reservation = new Reservation(
                        reservationID,
                        rs.getString("guest_id"),
                        rooms,
                        LocalDate.parse(rs.getString("check_in_date")),
                        LocalDate.parse(rs.getString("check_out_date")),
                        rs.getInt("number_of_guests"),
                        rs.getString("status")
                );
                bookings.add(reservation);
            }
            logger.info("Loaded " + bookings.size() + " current bookings.");
        } catch (SQLException e) {
            logger.severe("Error loading current bookings: " + e.getMessage());
        }
        return bookings;
    }

    public List<Reservation> getAllBookings() {
        List<Reservation> bookings = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM reservations";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String reservationID = rs.getString("reservation_id");
                List<Room> rooms = getRoomsForReservation(reservationID); // Helper method
                Reservation reservation = new Reservation(
                        reservationID,
                        rs.getString("guest_id"),
                        rooms,
                        LocalDate.parse(rs.getString("check_in_date")),
                        LocalDate.parse(rs.getString("check_out_date")),
                        rs.getInt("number_of_guests"),
                        rs.getString("status")
                );
                bookings.add(reservation);
            }
            logger.info("Loaded " + bookings.size() + " current bookings.");
        } catch (SQLException e) {
            logger.severe("Error loading current bookings: " + e.getMessage());
        }
        return bookings;
    }

    private List<Room> getRoomsForReservation(String reservationID) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.room_type, r.price " +
                "FROM rooms r " +
                "JOIN reservation_rooms rr ON r.room_id = rr.room_id " +
                "WHERE rr.reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(rs.getString("room_id"),
                        RoomType.valueOf(rs.getString("room_type")),
                        rs.getDouble("price")));
            }
        } catch (SQLException e) {
            logger.severe("Error fetching rooms for reservation " + reservationID + ": " + e.getMessage());
        }
        return rooms;
    }

    public String getGuestById(String guestID) {
        String sql = "SELECT name FROM guests WHERE guest_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guestID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            logger.severe("Error fetching guest " + guestID + ": " + e.getMessage());
        }
        return null;
    }

    public String getGuestNameByReservationID(String reservationID){
        String sql = "SELECT g.name FROM guests g " +
                "JOIN reservations r ON g.guest_id = r.guest_id " +
                "WHERE r.reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(Database.DB_JDBC + Database.DB_CONNECTION + Database.DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("name") : "Unknown Guest";
        } catch (SQLException e) {
            LoggerUtil.getLogger().severe("Error fetching guest name for reservation " + reservationID + ": " + e.getMessage());
            return "Error";
        }
    }

    public String searchGuestByName(String name) {
        String sql = "SELECT guest_id, name, phone_number, email, address FROM guests WHERE name LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String guestId = rs.getString("guest_id");
                String guestName = rs.getString("name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String address = rs.getString("address");

                // Build a detailed response string
                StringBuilder result = new StringBuilder();
                result.append("Guest ID: ").append(guestId)
                        .append(", Name: ").append(guestName);

                if (phoneNumber != null) {
                    result.append(", Phone: ").append(phoneNumber);
                }
                if (email != null) {
                    result.append(", Email: ").append(email);
                }
                if (address != null) {
                    result.append(", Address: ").append(address);
                }

                return result.toString();
            }
            return "Guest not found.";
        } catch (SQLException e) {
            logger.severe("Error searching guest " + name + ": " + e.getMessage());
            return "Error searching guest: " + e.getMessage();
        }
    }

    public LocalDate getCheckInDateByReservationID(String reservationID){
        String sql = "SELECT check_in_date FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(Database.DB_JDBC + Database.DB_CONNECTION + Database.DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? LocalDate.parse(rs.getString("check_in_date")) : null;
        } catch (SQLException e) {
            LoggerUtil.getLogger().severe("Error fetching check-in date for reservation " + reservationID + ": " + e.getMessage());
            return null;
        }
    }

    public int getNumberNightsByReservationID(String reservationID){
        String sql = "SELECT check_in_date, check_out_date FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(Database.DB_JDBC + Database.DB_CONNECTION + Database.DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalDate checkIn = LocalDate.parse(rs.getString("check_in_date"));
                LocalDate checkOut = LocalDate.parse(rs.getString("check_out_date"));
                return (int) DAYS.between(checkIn, checkOut);
            }
            return 0;
        } catch (SQLException e) {
            LoggerUtil.getLogger().severe("Error fetching number of nights for reservation " + reservationID + ": " + e.getMessage());
            return 0;
        }
    }

    public String getRooms(String reservationID) {
        String sql = "SELECT GROUP_CONCAT(r.room_id || ' (' || r.room_type || ')') as rooms " +
                "FROM reservation_rooms rr " +
                "JOIN rooms r ON rr.room_id = r.room_id " +
                "WHERE rr.reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(Database.DB_JDBC + Database.DB_CONNECTION + Database.DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("rooms") : "No Rooms";
        } catch (SQLException e) {
            LoggerUtil.getLogger().severe("Error fetching rooms for reservation " + reservationID + ": " + e.getMessage());
            return "Error";
        }
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT feedback_id, reservation_id, rating, comment FROM feedback";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String feedbackId = rs.getString("feedback_id");
                String reservationId = rs.getString("reservation_id");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                Feedback feedback = new Feedback(reservationId, rating, comment);
                feedbackList.add(feedback);
            }
            logger.info("Loaded " + feedbackList.size() + " feedback entries.");
            return feedbackList;
        } catch (SQLException e) {
            logger.severe("Error fetching feedback: " + e.getMessage());
            return feedbackList;
        }
    }

    public boolean deleteBooking(String reservationID) {
        String deleteRoomsSql = "DELETE FROM reservation_rooms WHERE reservation_id = ?";
        String deleteReservationSql = "DELETE FROM reservations WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME)) {
            conn.setAutoCommit(false);
            try (PreparedStatement psRooms = conn.prepareStatement(deleteRoomsSql);
                 PreparedStatement psReservation = conn.prepareStatement(deleteReservationSql)) {
                psRooms.setString(1, reservationID);
                psRooms.executeUpdate();
                psReservation.setString(1, reservationID);
                int rowsAffected = psReservation.executeUpdate();
                conn.commit();
                logger.info("Deleted booking: " + reservationID);
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                logger.severe("Error deleting booking " + reservationID + ": " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            logger.severe("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public double getPayableAmount(String reservationID) {
        String sql = "SELECT b.amount, b.tax, b.discount FROM billing b WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double amount = rs.getDouble("amount");
                double tax = rs.getDouble("tax");
                double discount = rs.getDouble("discount");
                return (amount + tax) * (1 - (discount/100));
            } else {
                logger.warning("No billing record found for reservation " + reservationID);
                return 0.0;
            }
        } catch (SQLException e) {
            logger.severe("Error fetching payable amount for reservation " + reservationID + ": " + e.getMessage());
            return 0.0;
        }
    }

    public void applyDiscount(Reservation reservation, double discount) {
        // Update the billing table with the new discount
        String sql = "UPDATE billing SET discount = ? WHERE reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, discount);
            ps.setString(2, reservation.getReservationID());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println();
                logger.info("Discount of $" + discount + " applied to reservation " + reservation.getReservationID());
            } else {
                logger.severe("Error finding billing record for " + reservation.getGuestID());
            }
        } catch (SQLException e) {
            System.err.println("Error applying discount: " + e.getMessage());
        }
    }

    public boolean checkOut(String reservationID) {
        String sql = "UPDATE reservations SET status = 'CHECKED-OUT' WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reservationID);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                logger.info("Reservation checked Out: " + reservationID);
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Error during check-out: " + e.getMessage());
            return false;
        }
    }

    public boolean checkIn(String reservationID) {
        String sql = "UPDATE reservations SET status = 'CHECKED-IN' WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reservationID);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                logger.info("Reservation checked In: " + reservationID);
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.severe("Error during check-out: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelBooking(String reservationID) {
        String sql = "UPDATE reservations SET status = 'CANCELED' WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reservationID);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Reservation Cancelled: " + reservationID);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.severe("Error during cancellation: " + e.getMessage());
            return false;
        }
    }

    public double getAverageFeedbackRating() {
        String sql = "SELECT AVG(rating) as avg_rating FROM feedback";
        try (Connection conn = DriverManager.getConnection(DB_JDBC + DB_CONNECTION + DB_NAME);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
            return 0.0;
        } catch (SQLException e) {
            logger.severe("Error calculating average feedback rating: " + e.getMessage());
            return 0.0;
        }
    }

}
