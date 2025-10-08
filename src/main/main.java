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

    //Admin
    public static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View All Users");
            System.out.println("2. Approve a Customer Account");
            System.out.println("3. Update a User Account");
            System.out.println("4. Delete a User Account");
            System.out.println("5. View All Bookings");
            System.out.println("6. Delete Bookings ");
            System.out.println("7. Logout");
            System.out.print("Enter choice (1-6): ");
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

                case 7:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    //Customer Menu
    public static void customerMenu(Map<String, Object> user) {
        int userId = Integer.parseInt(user.get("u_id").toString());
        while (true) {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Book Ticket");
            System.out.println("2. View My Bookings");
            System.out.println("3. Logout");
            System.out.print("Enter choice (1-3): ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1:
                    System.out.print("Enter Movie Name: ");
                    String movie = sc.next();
                    System.out.print("Enter Showtime: ");
                    String showtime = sc.next();
                    System.out.print("Enter Seat Number: ");
                    String seat = sc.next();
                    System.out.print("Enter Booking Fee: ");
                    double fee = sc.nextDouble();

                    String sql = "INSERT INTO tbl_booking(u_id, movie_name, showtime, seat_no, booking_fee) VALUES (?, ?, ?, ?, ?)";
                    con.addRecord(sql, userId, movie, showtime, seat, fee);
                    System.out.println("🎟️ Ticket booked successfully!");
                    break;

                case 2:
                    String view = "SELECT * FROM tbl_booking WHERE u_id = " + userId;
                    String[] headers = {"Booking ID", "Movie", "Showtime", "Seat", "Fee"};
                    String[] cols = {"u_id", "movie_name", "showtime", "seat_no", "booking_fee"};
                    con.viewRecords(view, headers, cols);
                    break;

                case 3:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

}
