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

    public static void viewAllUsers() {
        String sql = "SELECT * FROM user_table";
        String[] headers = {"ID", "Name", "Email", "Role", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_role", "u_status"};

        con.viewRecords(sql, headers, columns);
    }

    // Admin Menu
    public static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Manage Accounts");
            System.out.println("2. Manage Bookings");
            System.out.println("3. Manage Movies of the Month");
            System.out.println("4. Manage Transactions");
            System.out.println("5. Logout");
            System.out.print("Enter choice (1-5): ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter a number 1-5.");
                sc.nextLine();
                continue;
            }
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    manageAccounts();
                    break;

                case 2:
                    manageBookings();
                    break;

                case 3:
                    manageMovies();
                    break;

                case 4:
                    manageTransactions();
                    break;

                case 5:
                    return;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    // ------------------- MANAGE ACCOUNTS -------------------
    public static void manageAccounts() {
        while (true) {
            System.out.println("\n--- MANAGE ACCOUNTS ---");
            System.out.println("1. View All Users");
            System.out.println("2. Approve Customer Account");
            System.out.println("3. Update User Account");
            System.out.println("4. Delete User Account");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Enter choice (1-5): ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter a number 1-5.");
                sc.nextLine();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;

                case 2:
                    viewAllUsers();
                    System.out.print("Enter User ID to approve: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("❌ Invalid ID.");
                        sc.nextLine();
                        break;
                    }
                    int id = sc.nextInt();
                    sc.nextLine();
                    String upd = "UPDATE user_table SET u_status = ? WHERE u_id = ?";
                    con.updateRecord(upd, "Approved", id);
                    System.out.println("✅ User approved!");
                    break;

                case 3:
                    viewAllUsers();
                    System.out.print("Enter User ID to update: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("❌ Invalid ID.");
                        sc.nextLine();
                        break;
                    }
                    int uid = sc.nextInt();
                    sc.nextLine();

                    String checkUser = "SELECT 1 FROM user_table WHERE u_id = ?";
                    if (!con.recordExists(checkUser, uid)) {
                        System.out.println("⚠️ User not found.");
                        break;
                    }

                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine().trim();
                    System.out.print("Enter new email: ");
                    String newEmail = sc.nextLine().trim();
                    System.out.print("Enter new role (Admin/Customer): ");
                    String newRole = sc.nextLine().trim();

                    if (!newRole.equalsIgnoreCase("Admin") && !newRole.equalsIgnoreCase("Customer")) {
                        System.out.println("❌ Invalid role. Use 'Admin' or 'Customer'.");
                        break;
                    }

                    String sqlUpdateUser = "UPDATE user_table SET u_name = ?, u_email = ?, u_role = ? WHERE u_id = ?";
                    con.updateRecord(sqlUpdateUser, newName, newEmail, newRole, uid);
                    System.out.println("✅ User updated!");
                    break;

                case 4:
                    viewAllUsers();
                    System.out.print("Enter User ID to delete: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("❌ Invalid ID.");
                        sc.nextLine();
                        break;
                    }
                    int del = sc.nextInt();
                    sc.nextLine();

                    String checkUserDel = "SELECT 1 FROM user_table WHERE u_id = ?";
                    if (!con.recordExists(checkUserDel, del)) {
                        System.out.println("⚠️ User not found.");
                        break;
                    }

                    System.out.print("Are you sure you want to delete this user? (1-Yes, 0-No): ");
                    int confirm = sc.nextInt();
                    sc.nextLine();
                    if (confirm == 1) {
                        String sqlDel = "DELETE FROM user_table WHERE u_id = ?";
                        con.updateRecord(sqlDel, del);
                        System.out.println("🗑️ User deleted!");
                    } else {
                        System.out.println("❌ Deletion canceled.");
                    }
                    break;

                case 5:
                    return;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }

    // ------------------- MANAGE BOOKINGS -------------------
    public static void manageBookings() {
        while (true) {
            System.out.println("\n--- MANAGE BOOKINGS ---");
            System.out.println("1. View All Bookings");
            System.out.println("2. Delete Booking");
            System.out.println("3. Back to Admin Menu");
            System.out.print("Enter choice (1-3): ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter a number 1-3.");
                sc.nextLine();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    String view = "SELECT b.b_id, b.u_id, m.movie_name, m.showtime, b.seat_no, b.booking_fee "
                            + "FROM tbl_booking b "
                            + "JOIN tbl_movies m ON b.m_id = m.m_id";
                    String[] headers2 = {"Booking ID", "User ID", "Movie", "Showtime", "Seat", "Fee"};
                    String[] cols2 = {"b_id", "u_id", "movie_name", "showtime", "seat_no", "booking_fee"};
                    con.viewRecords(view, headers2, cols2);
                    break;

                case 2:
                    System.out.print("Enter Booking ID to delete: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("❌ Invalid ID.");
                        sc.nextLine();
                        break;
                    }
                    int bId = sc.nextInt();
                    sc.nextLine();

                    String checkBooking = "SELECT 1 FROM tbl_booking WHERE b_id = ?";
                    if (!con.recordExists(checkBooking, bId)) {
                        System.out.println("⚠️ Booking not found.");
                        break;
                    }

                    System.out.print("Are you sure you want to delete booking ID " + bId + "? (1-Yes, 0-No): ");
                    int confirmBook = sc.nextInt();
                    sc.nextLine();
                    if (confirmBook == 1) {
                        String sqlDelBooking = "DELETE FROM tbl_booking WHERE b_id = ?";
                        con.updateRecord(sqlDelBooking, bId);
                        System.out.println("🗑️ Booking deleted!");
                    } else {
                        System.out.println("❌ Deletion canceled.");
                    }
                    break;

                case 3:
                    return;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }

    public static void viewMovies() {
        String sqlView = "SELECT * FROM tbl_movies";
        String[] headers = {"Movie ID", "Movie Name", "Genre", "Run Time", "Show Date (MM/DD/YYYY)", "Available Seats"};
        String[] columns = {"m_id", "movie_name", "genre", "run_time", "showtime", "available_seats"};

        con.viewRecords(sqlView, headers, columns);
    }

    // MANAGE MOVIES
    public static void manageMovies() {
        while (true) {
            System.out.println("\n--- MANAGE MOVIES ---");
            System.out.println("1. View All Movies");
            System.out.println("2. Add New Movie(s)");
            System.out.println("3. Update Movie(s)");
            System.out.println("4. Delete Movie(s)");
            System.out.println("5. Go Back");
            System.out.print("Enter choice (1-5): ");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    viewMovies();
                    break;
//                    String sqlView = "SELECT * FROM tbl_movies";
//                    String[] headers = {"Movie ID", "Movie Name", "Genre", "Run Time", "Show Date (MM/DD/YYYY)", "Available Seats"};
//                    String[] cols = {"m_id", "movie_name", "genre", "run_time", "showtime", "available_seats"};
//                    con.viewRecords(sqlView, headers, cols);

                case 2: // ✅ Add Multiple Movies
                    System.out.print("How many movies do you want to add? ");
                    int addCount = sc.nextInt();
                    sc.nextLine();

                    for (int i = 0; i < addCount; i++) {
                        System.out.println("\n--- Add Movie #" + (i + 1) + " ---");
                        System.out.print("Enter Movie Name: ");
                        String movieName = sc.nextLine();
                        System.out.print("Enter Genre: ");
                        String genre = sc.nextLine();
                        System.out.print("Enter Run Time (e.g., 2hr 15min): ");
                        String runTime = sc.nextLine();
                        System.out.print("Enter Show Date (MM/DD/YYYY): ");
                        String showDate = sc.nextLine();

                        int seats = 50; // default seats
                        String sqlInsert = "INSERT INTO tbl_movies (movie_name, genre, run_time, showtime, available_seats) VALUES (?, ?, ?, ?, ?)";
                        con.updateRecord(sqlInsert, movieName, genre, runTime, showDate, seats);
                        System.out.println("🎬 Movie \"" + movieName + "\" added successfully with 50 seats!");
                    }
                    System.out.println("\n✅ All movies added successfully!");
                    break;

                case 3: // ✏️ Update Multiple Movies
                    viewMovies();
                    System.out.print("How many movies do you want to update? ");
                    int updateCount = sc.nextInt();
                    sc.nextLine();

                    for (int i = 0; i < updateCount; i++) {
                        System.out.println("\n--- Update Movie #" + (i + 1) + " ---");
                        System.out.print("Enter Movie ID to Update: ");
                        int mid = sc.nextInt();
                        sc.nextLine();

                        // Confirm movie exists
                        String checkMovie = "SELECT * FROM tbl_movies WHERE m_id = ?";
                        if (con.recordExists(checkMovie, mid)) {
                            System.out.print("Enter New Movie Name: ");
                            String newName = sc.nextLine();
                            System.out.print("Enter New Genre: ");
                            String newGenre = sc.nextLine();
                            System.out.print("Enter New Run Time (e.g., 2hr 15min): ");
                            String newRunTime = sc.nextLine();
                            System.out.print("Enter New Show Date (MM/DD/YYYY): ");
                            String newShowDate = sc.nextLine();
                            System.out.print("Enter New Available Seats: ");
                            int newSeats = sc.nextInt();
                            sc.nextLine();

                            String sqlUpdate = "UPDATE tbl_movies SET movie_name = ?, genre = ?, run_time = ?, showtime = ?, available_seats = ? WHERE m_id = ?";
                            con.updateRecord(sqlUpdate, newName, newGenre, newRunTime, newShowDate, newSeats, mid);
                            System.out.println("✅ Movie ID " + mid + " updated successfully!");
                        } else {
                            System.out.println("⚠️ Movie ID " + mid + " not found! Skipping...");
                        }
                    }
                    System.out.println("\n✅ Movie updates completed!");
                    break;

                case 4: // 🗑️ Delete Multiple Movies
                    viewMovies();
                    System.out.print("How many movies do you want to delete? ");
                    int deleteCount = sc.nextInt();
                    sc.nextLine();

                    for (int i = 0; i < deleteCount; i++) {
                        System.out.println("\n--- Delete Movie #" + (i + 1) + " ---");
                        System.out.print("Enter Movie ID to Delete: ");
                        int del = sc.nextInt();
                        sc.nextLine();

                        String checkMovie = "SELECT * FROM tbl_movies WHERE m_id = ?";
                        if (!con.recordExists(checkMovie, del)) {
                            System.out.println("⚠️ Movie ID " + del + " not found! Skipping...");
                            continue;
                        }

                        System.out.print("Are you sure you want to delete this movie? (1-Yes, 0-No): ");
                        int confirmMovie = sc.nextInt();
                        sc.nextLine();

                        while (confirmMovie != 1 && confirmMovie != 0) {
                            System.out.print("Invalid input. Enter 1 for Yes or 0 for No: ");
                            confirmMovie = sc.nextInt();
                            sc.nextLine();
                        }

                        if (confirmMovie == 1) {
                            String sqlDel = "DELETE FROM tbl_movies WHERE m_id = ?";
                            con.updateRecord(sqlDel, del);
                            System.out.println("🗑️ Movie ID " + del + " deleted!");
                        } else {
                            System.out.println("❌ Movie deletion canceled for ID " + del);
                        }
                    }
                    System.out.println("\n✅ Bulk deletion completed!");
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // Manage Transactions
    public static void manageTransactions() {
        while (true) {
            System.out.println("\n--- MANAGE TRANSACTIONS ---");
            System.out.println("1. View All Transactions");
            System.out.println("2. Update Single Transaction Payment");
            System.out.println("3. Update All Transactions by User");
            System.out.println("4. Go Back");
            System.out.print("Enter choice (1-4): ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter a number 1-4.");
                sc.nextLine();
                continue;
            }

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    String viewTrans = "SELECT u.u_id, u.u_name, u.u_email, u.u_loyalty, "
                            + "m.movie_name, m.showtime, b.seat_no, t.booking_fee, "
                            + "t.payment_status, t.payment_date "
                            + "FROM tbl_transaction t "
                            + "JOIN tbl_booking b ON t.b_id = b.b_id "
                            + "JOIN user_table u ON b.u_id = u.u_id "
                            + "JOIN tbl_movies m ON b.m_id = m.m_id "
                            + "ORDER BY u.u_id, t.t_id";

                    List<Map<String, Object>> data = con.fetchRecords(viewTrans);

                    if (data.isEmpty()) {
                        System.out.println("No transactions found.");
                        break;
                    }

                    int currentUserId = -1;

                    for (Map<String, Object> row : data) {
                        int userId = Integer.parseInt(row.get("u_id").toString());

                        // Display new user header once
                        if (userId != currentUserId) {
                            currentUserId = userId;
                            System.out.println("\n===============================");
                            System.out.println("Customer: " + row.get("u_name") + " (ID: " + row.get("u_id") + ")");
                            System.out.println("Email: " + row.get("u_email"));
                            System.out.println("Loyalty: " + row.get("u_loyalty"));
                            System.out.println("-------------------------------");
                        }

                        // Display transaction details
                        System.out.println("Movie: " + row.get("movie_name"));
                        System.out.println("Seat: " + row.get("seat_no"));
                        System.out.println("Fee: " + row.get("booking_fee"));
                        System.out.println("Status: " + row.get("payment_status"));
                        System.out.println("Date: " + row.get("payment_date"));
                        System.out.println("===============================");
                    }
                    break;

                case 2:
                    System.out.print("Enter Transaction ID to update: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("❌ Invalid ID.");
                        sc.nextLine();
                        break;
                    }
                    int tid = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Select new payment status:");
                    System.out.println("1. Paid");
                    System.out.println("2. Not Yet Paid");
                    System.out.print("Enter choice (1-2): ");
                    int psChoice = sc.nextInt();
                    sc.nextLine();

                    String newStatus = (psChoice == 1) ? "Paid" : "Not Yet Paid";
                    String sqlUpdate = "UPDATE tbl_transaction SET payment_status = ? WHERE t_id = ?";
                    con.updateRecord(sqlUpdate, newStatus, tid);
                    System.out.println("✅ Payment status updated successfully!");
                    break;

                case 3:
                    System.out.print("Enter User ID to update all transactions: ");
                    if (!sc.hasNextInt()) {
                        System.out.println("❌ Invalid ID.");
                        sc.nextLine();
                        break;
                    }
                    int uid = sc.nextInt();
                    sc.nextLine();

                    // Check if user has existing transactions
                    String checkUser = "SELECT 1 FROM tbl_transaction t "
                            + "JOIN tbl_booking b ON t.b_id = b.b_id "
                            + "WHERE b.u_id = ?";
                    if (!con.recordExists(checkUser, uid)) {
                        System.out.println("⚠️ No transactions found for this user.");
                        break;
                    }

                    System.out.println("Select new payment status for all transactions:");
                    System.out.println("1. Paid");
                    System.out.println("2. Not Yet Paid");
                    System.out.print("Enter choice (1-2): ");
                    int userStatusChoice = sc.nextInt();
                    sc.nextLine();

                    String userStatus = (userStatusChoice == 1) ? "Paid" : "Not Yet Paid";

                    String sqlUpdateUser = "UPDATE tbl_transaction "
                            + "SET payment_status = ? "
                            + "WHERE b_id IN (SELECT b_id FROM tbl_booking WHERE u_id = ?)";
                    con.updateRecord(sqlUpdateUser, userStatus, uid);

                    System.out.println("✅ All transactions for User ID " + uid + " updated to '" + userStatus + "'.");
                    break;

                case 4:
                    return;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
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
            System.out.println("5. View Canceled Tickets");
            System.out.println("6. Logout");
            System.out.print("Enter choice (1-6): ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1:
                    viewMovies();
//                    String sqlMovies = "SELECT * FROM tbl_movies";
//                    String[] headers = {"Movie ID", "Movie Name", "Genre", "Run Time", "Showtime", "Available Seats"};
//                    String[] cols = {"m_id", "movie_name", "genre", "run_time", "showtime", "available_seats"};
//                    con.viewRecords(sqlMovies, headers, cols);
                    break;

                case 2:
                    // View movies before booking
                    String sqlMoviesList = "SELECT * FROM tbl_movies";
                    String[] movieHeaders = {"Movie ID", "Movie Name", "Genre", "Run Time", "Showtime", "Available Seats"};
                    String[] movieCols = {"m_id", "movie_name", "genre", "run_time", "showtime", "available_seats"};
                    con.viewRecords(sqlMoviesList, movieHeaders, movieCols);

                    System.out.print("Enter Movie ID to Book: ");
                    int movieId = sc.nextInt();

                    String getMovie = "SELECT * FROM tbl_movies WHERE m_id = ?";
                    List<Map<String, Object>> movieData = con.fetchRecords(getMovie, movieId);
                    if (movieData == null || movieData.isEmpty()) {
                        System.out.println("❌ Invalid Movie ID!");
                        break;
                    }

                    Map<String, Object> selectedMovie = movieData.get(0);
                    String movieName = selectedMovie.get("movie_name").toString();
                    String showtime = selectedMovie.get("showtime").toString();
                    String runTime = selectedMovie.get("run_time") != null ? selectedMovie.get("run_time").toString() : "N/A";
                    int availableSeats = Integer.parseInt(selectedMovie.get("available_seats").toString());

                    System.out.println("🎬 " + movieName + " | Duration: " + runTime + " | Showtime: " + showtime);
                    System.out.println("Available seats: " + availableSeats);

                    System.out.print("How many tickets would you like to book? ");
                    int ticketCount = sc.nextInt();

                    if (ticketCount > availableSeats || ticketCount <= 0) {
                        System.out.println("❌ Invalid number of tickets!");
                        break;
                    }

                    double ticketPrice = 150.0;
                    double totalFee = ticketPrice * ticketCount;
                    StringBuilder seatsBooked = new StringBuilder();

                    // Loop for multiple seats
                    for (int i = 1; i <= ticketCount; i++) {
                        System.out.print("Enter seat number #" + i + ": ");
                        String seatNo = sc.next();

                        // Check if seat taken
                        String checkSeat = "SELECT * FROM tbl_booking WHERE m_id = ? AND seat_no = ? AND canceled = 0";
                        List<Map<String, Object>> existingSeat = con.fetchRecords(checkSeat, movieId, seatNo);
                        if (existingSeat != null && !existingSeat.isEmpty()) {
                            System.out.println("❌ Seat " + seatNo + " is already taken!");
                            i--;
                            continue;
                        }

                        // Add booking
                        String sqlBook = "INSERT INTO tbl_booking(u_id, m_id, seat_no, booking_fee, canceled) VALUES (?, ?, ?, ?, 0)";
                        con.addRecord(sqlBook, userId, movieId, seatNo, ticketPrice);

                        // Get the latest booking ID for this seat
                        String getBId = "SELECT b_id FROM tbl_booking ORDER BY b_id DESC LIMIT 1";
                        List<Map<String, Object>> bResult = con.fetchRecords(getBId);
                        int bId = Integer.parseInt(bResult.get(0).get("b_id").toString());

                        // Create transaction with default status “Not Yet Paid”
                        String insertTrans = "INSERT INTO tbl_transaction (b_id, booking_fee, payment_status, payment_date) VALUES (?, ?, ?, ?)";
                        String dateNow = java.time.LocalDateTime.now().toString();
                        con.addRecord(insertTrans, bId, ticketPrice, "Not Yet Paid", dateNow);

                        seatsBooked.append(seatNo).append(" ");
                    }

                    // Update available seats
                    String updateSeats = "UPDATE tbl_movies SET available_seats = available_seats - ? WHERE m_id = ?";
                    con.updateRecord(updateSeats, ticketCount, movieId);

                    // Print booking receipt
                    System.out.println("\n--- 🎫 BOOKING RECEIPT ---");
                    System.out.println("Customer: " + user.get("u_name"));
                    System.out.println("Movie: " + movieName);
                    System.out.println("Duration: " + runTime);
                    System.out.println("Showtime: " + showtime);
                    System.out.println("Seats: " + seatsBooked.toString().trim());
                    System.out.println("Total Fee: ₱" + totalFee);
                    System.out.println("Payment Status: NOT YET PAID");
                    System.out.println("------------------------------");
                    System.out.println("📅 Date: " + java.time.LocalDate.now());
                    System.out.println("Thank you for booking with us!\n");
                    break;

                case 3:
                    System.out.println("\n--- 🎟️ MY ACTIVE BOOKINGS ---");

                    String myActiveSql = ""
                            + "SELECT u.u_id, u.u_name, b.b_id, m.movie_name, m.run_time, m.showtime, "
                            + "b.seat_no, b.booking_fee, t.payment_status, t.payment_date "
                            + "FROM tbl_booking b "
                            + "JOIN user_table u ON b.u_id = u.u_id "
                            + "JOIN tbl_movies m ON b.m_id = m.m_id "
                            + "LEFT JOIN tbl_transaction t ON b.b_id = t.b_id "
                            + "WHERE b.u_id = ? AND b.canceled = 0";

                    List<Map<String, Object>> activeBookings = con.fetchRecords(myActiveSql, userId);

                    if (activeBookings == null || activeBookings.isEmpty()) {
                        System.out.println("⚠️ You have no active bookings.");
                        break;
                    }

                    System.out.println("\n==================== BOOKING RECEIPTS ====================\n");

                    for (Map<String, Object> b : activeBookings) {
                        System.out.println("🎟️ MOVIE TICKET RECEIPT");
                        System.out.println("----------------------------------------------------------");
                        System.out.println("🎫 BOOKING ID  : " + b.get("b_id"));
                        System.out.println("👤 USER ID     : " + b.get("u_id"));
                        System.out.println("🧾 NAME        : " + b.get("u_name"));
                        System.out.println("📅 PAYMENT DATE: " + (b.get("payment_date") == null ? "N/A" : b.get("payment_date")));
                        System.out.println("🎬 MOVIE TITLE : " + b.get("movie_name"));
                        System.out.println("⏱ RUN TIME    : " + b.get("run_time"));
                        System.out.println("🕒 SHOWTIME    : " + b.get("showtime"));
                        System.out.println("💺 SEAT NUMBER : " + b.get("seat_no"));
                        System.out.println("💰 BOOKING FEE : ₱" + b.get("booking_fee"));
                        System.out.println("💳 PAYMENT     : " + (b.get("payment_status") == null ? "Not Yet Paid" : b.get("payment_status")));
                        System.out.println("----------------------------------------------------------\n");
                    }

                    System.out.println("==========================================================\n");
                    break;

                case 4:
                    System.out.print("Enter Booking ID to Cancel: ");
                    int cancelId = sc.nextInt();

                    String fetchBooking = "SELECT * FROM tbl_booking WHERE b_id = ? AND u_id = ?";
                    List<Map<String, Object>> bookingData = con.fetchRecords(fetchBooking, cancelId, userId);
                    if (bookingData == null || bookingData.isEmpty()) {
                        System.out.println("❌ Booking not found or does not belong to you.");
                        break;
                    }

                    Map<String, Object> booking = bookingData.get(0);
                    int bookedMId = Integer.parseInt(booking.get("m_id").toString());
                    int isCanceled = Integer.parseInt(booking.get("canceled").toString());
                    if (isCanceled == 1) {
                        System.out.println("❌ Booking already canceled.");
                        break;
                    }

                    // Cancel booking
                    String cancelSQL = "UPDATE tbl_booking SET canceled = 1 WHERE b_id = ?";
                    con.updateRecord(cancelSQL, cancelId);

                    // Restore seat
                    String restoreSeatSql = "UPDATE tbl_movies SET available_seats = available_seats + 1 WHERE m_id = ?";
                    con.updateRecord(restoreSeatSql, bookedMId);

                    System.out.println("🛑 Ticket canceled! Seat restored.");
                    break;

                case 5:
                    System.out.println("\n--- ❌ CANCELED TICKETS ---");
                    String canceledSql = ""
                            + "SELECT b.b_id, m.movie_name, m.showtime, b.seat_no, b.booking_fee "
                            + "FROM tbl_booking b "
                            + "JOIN tbl_movies m ON b.m_id = m.m_id "
                            + "WHERE b.u_id = ? AND b.canceled = 1";
                    List<Map<String, Object>> canceledBookings = con.fetchRecords(canceledSql, userId);

                    if (canceledBookings == null || canceledBookings.isEmpty()) {
                        System.out.println("You have no canceled tickets.");
                        break;
                    }

                    System.out.printf("%-10s %-20s %-20s %-10s %-10s\n", "Book ID", "Movie", "Showtime", "Seat", "Fee");
                    for (Map<String, Object> b : canceledBookings) {
                        System.out.printf("%-10s %-20s %-20s %-10s %-10s\n",
                                b.get("b_id"), b.get("movie_name"), b.get("showtime"), b.get("seat_no"), b.get("booking_fee"));
                    }
                    break;

                case 6:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

}
