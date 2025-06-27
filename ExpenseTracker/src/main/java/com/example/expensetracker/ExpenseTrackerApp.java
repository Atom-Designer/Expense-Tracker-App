package com.example.expensetracker;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExpenseTrackerApp {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Transaction> transactions = new ArrayList<>();
    static User currentUser = null;

    public static void main(String[] args) {
        login();

        loadTransactions();

        while (true) {
            System.out.println("\n=== Expense Tracker Menu ===");
            System.out.println("1. Add Transaction");
            System.out.println("2. View All Transactions");
            System.out.println("3. Show Summary");
            System.out.println("4. Show Total by Category");
            System.out.println("5. View by Month");
            System.out.println("6. Search by Category/Keyword");
            System.out.println("7. Export to CSV");
            System.out.println("8. Show Chart (JavaFX)");
            System.out.println("9. Logout & Exit");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt(); scanner.nextLine();

            switch (choice) {
                case 1 -> addTransaction();
                case 2 -> viewAll();
                case 3 -> showSummary();
                case 4 -> showByCategory();
                case 5 -> filterByMonth();
                case 6 -> searchByKeyword();
                case 7 -> exportToCSV();
                case 8 -> ExpenseChart.launchChart(transactions);
                case 9 -> {
                    saveTransactions();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Login or signup
    public static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        File file = new File("users.csv");
        try {
            if (!file.exists()) file.createNewFile();
            Scanner fileScanner = new Scanner(file);
            boolean found = false;

            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split(",", 2);
                if (data[0].equals(username) && data[1].equals(password)) {
                    currentUser = new User(username, password);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // New user
                FileWriter fw = new FileWriter(file, true);
                fw.write(username + "," + password + "\n");
                fw.close();
                currentUser = new User(username, password);
                System.out.println("✅ New user created.");
            } else {
                System.out.println("✅ Login successful.");
            }

        } catch (IOException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    public static void addTransaction() {
        System.out.print("Type (Income/Expense): ");
        String type = scanner.nextLine();

        System.out.print("Category: ");
        String category = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Amount: ");
        double amount = scanner.nextDouble(); scanner.nextLine();

        transactions.add(new Transaction(LocalDate.now(), type, category, description, amount));
        System.out.println("✅ Transaction added.");
    }

    public static void viewAll() {
        if (transactions.isEmpty()) System.out.println("No transactions.");
        else transactions.forEach(System.out::println);
    }

    public static void showSummary() {
        double income = 0, expense = 0;
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("Income")) income += t.getAmount();
            else expense += t.getAmount();
        }
        System.out.printf("Total Income: $%.2f | Total Expense: $%.2f | Balance: $%.2f\n", income, expense, income - expense);
    }

    public static void showByCategory() {
        HashMap<String, Double> map = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("Expense")) {
                map.put(t.getCategory(), map.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        map.forEach((k, v) -> System.out.printf("%-15s : $%.2f\n", k, v));
    }

    public static void filterByMonth() {
        System.out.print("Enter year: ");
        int year = scanner.nextInt();
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt(); scanner.nextLine();

        double income = 0, expense = 0;
        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                System.out.println(t);
                if (t.getType().equalsIgnoreCase("Income")) income += t.getAmount();
                else expense += t.getAmount();
            }
        }
        System.out.printf("For %d-%02d → Income: $%.2f | Expense: $%.2f | Balance: $%.2f\n", year, month, income, expense, income - expense);
    }

    public static void searchByKeyword() {
        System.out.print("Enter keyword or category: ");
        String keyword = scanner.nextLine().toLowerCase();
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategory().toLowerCase().contains(keyword) || t.getDescription().toLowerCase().contains(keyword)) {
                System.out.println(t);
                total += t.getAmount();
            }
        }
        System.out.printf("Total matching: $%.2f\n", total);
    }

    public static void exportToCSV() {
        try {
            FileWriter writer = new FileWriter(currentUser.getUsername() + "_export.csv");
            writer.write("Date,Type,Category,Description,Amount\n");
            for (Transaction t : transactions) {
                writer.write(t.getDate() + "," + t.getType() + "," + t.getCategory() + "," +
                        t.getDescription() + "," + t.getAmount() + "\n");
            }
            writer.close();
            System.out.println("✅ Exported to " + currentUser.getUsername() + "_export.csv");
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    public static void saveTransactions() {
        try {
            FileWriter writer = new FileWriter(currentUser.getUsername() + "_data.csv");
            for (Transaction t : transactions) {
                writer.write(t.getDate() + "," + t.getType() + "," + t.getCategory() + "," +
                        t.getDescription() + "," + t.getAmount() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public static void loadTransactions() {
        try {
            File file = new File(currentUser.getUsername() + "_data.csv");
            if (!file.exists()) return;

            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",", 5);
                transactions.add(new Transaction(LocalDate.parse(parts[0]), parts[1], parts[2], parts[3], Double.parseDouble(parts[4])));
            }
            sc.close();
            System.out.println("✅ Transactions loaded.");
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
