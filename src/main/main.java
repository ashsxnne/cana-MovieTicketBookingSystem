package main;

import config.config;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class main {

    static config con = new config();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("=== MOVIE TICKET BOOKING SYSTEM ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice (1-3): ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        } while (true);
    }

    //Register
    public static void register() {
        System.out.print("Enter Name: ");
        String name = sc.next();
        System.out.print("Enter Email: ");
        String email = sc.next();

        String checkEmailSql = "SELECT * FROM user_table WHERE u_email = ?";
        List<Map<String, Object>> existingUser = con.fetchRecords(checkEmailSql, email);

        if (existingUser != null && !existingUser.isEmpty()) {
            System.out.println("❌ This email is already in use. Please choose another email.");
            return;
        }

        System.out.print("Enter Password: ");
        String pass = sc.next();

        System.out.println("Choose Role:");
        System.out.println("1. Customer");
        System.out.println("2. Admin");
        System.out.print("Enter choice (1-2): ");
        int roleChoice = sc.nextInt();

        String role = (roleChoice == 2) ? "Admin" : "Customer";
        String sql = "INSERT INTO user_table(u_name, u_email, u_pass, u_role, u_status) VALUES(?, ?, ?, ?, ?)";
        con.addRecord(sql, name, email, pass, role, "Pending");

        System.out.println("✅ Registration successful! Waiting for approval.");
    }

    //Login
    public static void login() {
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Password: ");
        String pass = sc.next();

        String sql = "SELECT * FROM user_table WHERE u_email = ? AND u_pass = ?";
        List<Map<String, Object>> users = con.fetchRecords(sql, email, pass);

        if (users == null || users.isEmpty()) {
            System.out.println("❌ Invalid credentials!");
            return;
        }

        Map<String, Object> user = users.get(0);
        String role = user.get("u_role").toString();
        String status = user.get("u_status").toString();

        if (status.equals("Pending")) {
            System.out.println("Account still pending approval!");
            return;
        }

        System.out.println("Login successful! Welcome " + user.get("u_name") + " (" + role + ")");

        if (role.equalsIgnoreCase("Admin")) {
            adminMenu();
        } else {
            customerMenu(user);
        }
    }

    //Admin Menu
    public static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View All Users");
            System.out.println("2. Approve a Customer Account");
            System.out.println("3. Update a User Account");
            System.out.println("4. Delete a User Account");
            System.out.println("5. View All Bookings");
            System.out.println("6. Delete Bookings");
            System.out.println("7. Manage Movies of the Month");
            System.out.println("8. Logout");
            System.out.print("Enter choice (1-8): ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1:
                    String sql1 = "SELECT * FROM user_table";
                    String[] headers1 = {"ID", "Name", "Email", "Role", "Status"};
                    String[] cols1 = {"u_id", "u_name", "u_email", "u_role", "u_status"};
                    con.viewRecords(sql1, headers1, cols1);
                    break;

                case 2:
                    System.out.print("Enter User ID to approve: ");
                    int id = sc.nextInt();
                    String upd = "UPDATE user_table SET u_status = ? WHERE u_id = ?";
                    con.updateRecord(upd, "Approved", id);
                    System.out.println("✅ User approved!");
                    break;

                case 3:
                    System.out.print("Enter User ID to update: ");
                    int uid = sc.nextInt();
                    System.out.print("Enter new name: ");
                    String newName = sc.next();
                    System.out.print("Enter new email: ");
                    String newEmail = sc.next();
                    System.out.print("Enter new role (Admin/Customer): ");
                    String newRole = sc.next();
                    String sqlUpdateUser = "UPDATE user_table SET u_name = ?, u_email = ?, u_role = ? WHERE u_id = ?";
                    con.updateRecord(sqlUpdateUser, newName, newEmail, newRole, uid);
                    System.out.println("✅ User updated!");
                    break;

                case 4:
                    System.out.print("Enter User ID to delete: ");
                    int del = sc.nextInt();
                    String sqlDel = "DELETE FROM user_table WHERE u_id = ?";
                    con.updateRecord(sqlDel, del);
                    System.out.println("🗑️ User deleted!");
                    break;

                case 5:
                    String view = "SELECT * FROM tbl_booking";
                    String[] headers2 = {"Booking ID", "User ID", "Movie", "Showtime", "Seat", "Fee"};
                    String[] cols2 = {"b_id", "u_id", "movie_name", "showtime", "seat_no", "booking_fee"};
                    con.viewRecords(view, headers2, cols2);
                    break;

                case 6:
                    System.out.println("Enter Book ID to delete: ");
                    int dele = sc.nextInt();
                    sqlDel = "DELETE FROM tbl_booking WHERE b_id = ?";
                    con.updateRecord(sqlDel, dele);
                    System.out.println("🗑️ Book ID deleted!");
                    break;

                case 7:
                    manageMovies();
                    break;

                case 8:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // MANAGE MOVIES OF THE MONTH
// MANAGE MOVIES OF THE MONTH
public static void manageMovies() {
    while (true) {
        System.out.println("\n--- MANAGE MOVIES ---");
        System.out.println("1. View All Movies");
        System.out.println("2. Add New Movie");
        System.out.println("3. Update Movie");
        System.out.println("4. Delete Movie");
        System.out.println("5. Go Back");
        System.out.print("Enter choice (1-5): ");
        int ch = sc.nextInt();
        sc.nextLine();

        switch (ch) {
            case 1:
                System.out.println("Connection Successful");
                String sqlView = "SELECT * FROM tbl_movies";
                String[] headers = {"Movie ID", "Movie Name", "Genre", "Showtime", "Available Seats"};
                String[] cols = {"m_id", "movie_name", "genre", "showtime", "available_seats"};
                con.viewRecords(sqlView, headers, cols);
                break;

            case 2:
                System.out.println("Connection Successful");
                System.out.print("Enter Movie Name: ");
                String movieName = sc.nextLine();

                System.out.print("Enter Genre: ");
                String genre = sc.nextLine();

                System.out.print("Enter Showtime (e.g., 2hr): ");
                String showtime = sc.nextLine();

                int seats = 50; // default
                String sqlInsert = "INSERT INTO tbl_movies (movie_name, genre, showtime, available_seats) VALUES (?, ?, ?, ?)";
                con.updateRecord(sqlInsert, movieName, genre, showtime, seats);
                System.out.println("🎬 Movie added successfully with 50 available seats!");
                break;

            case 3:
                System.out.print("Enter Movie ID to Update: ");
                int mid = sc.nextInt();
                sc.nextLine();

                System.out.print("Enter New Movie Name: ");
                String newName = sc.nextLine();

                System.out.print("Enter New Genre: ");
                String newGenre = sc.nextLine();

                System.out.print("Enter New Showtime: ");
                String newShowtime = sc.nextLine();

                System.out.print("Enter New Available Seats: ");
                int newSeats = sc.nextInt();

                String sqlUpdate = "UPDATE tbl_movies SET movie_name = ?, genre = ?, showtime = ?, available_seats = ? WHERE m_id = ?";
                con.updateRecord(sqlUpdate, newName, newGenre, newShowtime, newSeats, mid);
                System.out.println("✅ Movie updated successfully!");
                break;

            case 4:
                System.out.print("Enter Movie ID to Delete: ");
                int del = sc.nextInt();

                String sqlDel = "DELETE FROM tbl_movies WHERE m_id = ?";
                con.updateRecord(sqlDel, del);
                System.out.println("🗑️ Movie deleted successfully!");
                break;

            case 5:
                return;

            default:
                System.out.println("Invalid choice!");
        }
    }
}


    // Customer Menu 
public static void customerMenu(Map<String, Object> user) {
    int userId = Integer.parseInt(user.get("u_id").toString());
    while (true) {
        System.out.println("\n--- CUSTOMER MENU ---");
        System.out.println("1. View Available Movies");
        System.out.println("2. Book Ticket");
        System.out.println("3. View My Bookings");
        System.out.println("4. Cancel Ticket");
        System.out.println("5. Logout");
        System.out.print("Enter choice (1-5): ");
        int ch = sc.nextInt();

        switch (ch) {
            case 1:
                String sqlMovies = "SELECT * FROM tbl_movies";
                String[] headers = {"Movie ID", "Movie Name", "Genre", "Showtime", "Available Seats"};
                String[] cols = {"m_id", "movie_name", "genre", "showtime", "available_seats"};
                con.viewRecords(sqlMovies, headers, cols);
                break;

            case 2:
                // Show available movies
                String sqlMoviesList = "SELECT * FROM tbl_movies";
                String[] movieHeaders = {"Movie ID", "Movie Name", "Genre", "Showtime", "Available Seats"};
                String[] movieCols = {"m_id", "movie_name", "genre", "showtime", "available_seats"};
                con.viewRecords(sqlMoviesList, movieHeaders, movieCols);

                // Select movie by ID
                System.out.print("Enter Movie ID to Book: ");
                int movieId = sc.nextInt();

                // Fetch movie details
                String getMovie = "SELECT * FROM tbl_movies WHERE m_id = ?";
                List<Map<String, Object>> movieData = con.fetchRecords(getMovie, movieId);
                if (movieData.isEmpty()) {
                    System.out.println("❌ Invalid Movie ID!");
                    break;
                }

                Map<String, Object> selectedMovie = movieData.get(0);
                String movieName = selectedMovie.get("movie_name").toString();
                String showtime = selectedMovie.get("showtime").toString();
                // handle possible null available_seats safely
                int availableSeats = 0;
                Object avObj = selectedMovie.get("available_seats");
                if (avObj != null) {
                    availableSeats = Integer.parseInt(avObj.toString());
                }

                System.out.println("🎬 " + movieName + " | Showtime: " + showtime);
                System.out.println("Available seats: " + availableSeats);

                // Input number of tickets
                System.out.print("How many tickets would you like to book? ");
                int ticketCount = sc.nextInt();

                if (ticketCount > availableSeats) {
                    System.out.println("❌ Not enough seats available!");
                    break;
                }

                double ticketPrice = 150.0;
                double totalFee = ticketPrice * ticketCount;
                System.out.println("💰 Your booking fee is ₱" + ticketPrice + " × " + ticketCount + " = ₱" + totalFee);

                // Pick seats (ensure seat uniqueness)
                StringBuilder seatsBooked = new StringBuilder();
                for (int i = 1; i <= ticketCount; i++) {
                    System.out.print("Enter seat number #" + i + ": ");
                    String seatNo = sc.next();

                    // Check if seat is already booked for this movie & showtime and not canceled
                    String checkSeat = "SELECT * FROM tbl_booking WHERE movie_name = ? AND showtime = ? AND seat_no = ? AND canceled = 0";
                    List<Map<String, Object>> existingSeat = con.fetchRecords(checkSeat, movieName, showtime, seatNo);

                    if (!existingSeat.isEmpty()) {
                        System.out.println("❌ Seat " + seatNo + " is already taken! Try another seat.");
                        i--;
                        continue;
                    }

                    // Add booking per seat — store booking_fee as ticketPrice (per seat)
                    String sqlBook = "INSERT INTO tbl_booking(u_id, movie_name, showtime, seat_no, booking_fee, canceled) VALUES (?, ?, ?, ?, ?, 0)";
                    con.addRecord(sqlBook, userId, movieName, showtime, seatNo, ticketPrice);
                    seatsBooked.append(seatNo).append(" ");
                }

                // Reduce available seats in tbl_movies
                String updateSeats = "UPDATE tbl_movies SET available_seats = available_seats - ? WHERE m_id = ?";
                con.updateRecord(updateSeats, ticketCount, movieId);

                System.out.println("🎟️ Successfully booked " + ticketCount + " ticket(s) for " + movieName + "!");
                System.out.println("🪑 Seats: " + seatsBooked.toString().trim());
                System.out.println("💸 Total Fee: ₱" + totalFee);
                break;

            case 3:
                String view = "SELECT * FROM tbl_booking WHERE u_id = " + userId;
                String[] headers2 = {"Booking ID", "Movie", "Showtime", "Seat", "Fee", "Canceled"};
                String[] cols2 = {"b_id", "movie_name", "showtime", "seat_no", "booking_fee", "canceled"};
                con.viewRecords(view, headers2, cols2);
                break;

            case 4:
                System.out.print("Enter Booking ID to Cancel: ");
                int cancelId = sc.nextInt();

                // Fetch the booking to ensure it belongs to user and is not already canceled
                String fetchBooking = "SELECT * FROM tbl_booking WHERE b_id = ? AND u_id = ?";
                List<Map<String, Object>> bookingData = con.fetchRecords(fetchBooking, cancelId, userId);
                if (bookingData.isEmpty()) {
                    System.out.println("❌ Booking not found or does not belong to you.");
                    break;
                }

                Map<String, Object> booking = bookingData.get(0);
                Object canceledObj = booking.get("canceled");
                int isCanceled = 0;
                if (canceledObj != null) isCanceled = Integer.parseInt(canceledObj.toString());
                if (isCanceled == 1) {
                    System.out.println("❌ Booking is already canceled.");
                    break;
                }

                String bookedMovie = booking.get("movie_name").toString();
                String bookedShowtime = booking.get("showtime").toString();

                // Mark booking as canceled (use 1 for true in SQLite)
                String cancelSQL = "UPDATE tbl_booking SET canceled = 1 WHERE b_id = ? AND u_id = ?";
                con.updateRecord(cancelSQL, cancelId, userId);

                // Return the seat to available pool (increase available_seats by 1)
                String restoreSeatSql = "UPDATE tbl_movies SET available_seats = available_seats + 1 WHERE movie_name = ? AND showtime = ?";
                con.updateRecord(restoreSeatSql, bookedMovie, bookedShowtime);

                System.out.println("🛑 Ticket canceled! Note: Booking fee is non-refundable.");
                break;

            case 5:
                return;

            default:
                System.out.println("Invalid choice!");
        }
    }
}

}
