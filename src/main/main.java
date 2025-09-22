package main;

import config.config;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        config c = new config();
        c.connectDB();

        String name, email, pn;
        int choice = 0;
        String input;

        do {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Enter and Insert User Details");
            System.out.println("2. Exit");
            System.out.print("Enter your choice (1 or 2): ");
            input = s.nextLine(); 

            if (input.matches("[0-9]+")) {  
                choice = Integer.parseInt(input);
            } else {
                System.out.println("❌ Invalid input. Please enter a number (1 or 2).");
                continue; 
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    name = s.nextLine();

                    System.out.print("Enter Email: ");
                    email = s.nextLine();

                    System.out.print("Enter Phone Number: ");
                    pn = s.nextLine();

                    String sql = "INSERT INTO user_table (user_n, user_email, user_contactnum) VALUES (?, ?, ?)";
                    c.addRecord(sql, name, email, pn);

                    System.out.println("✅ User inserted successfully!");
                    break;

                case 2:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                    break;
            }
        } while (choice != 2);

        s.close();
    }
}