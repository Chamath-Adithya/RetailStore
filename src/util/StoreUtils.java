package util;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import model.*;

public class StoreUtils {
    public static double calculateDiscount(double total, double discountRate) {
        return total * discountRate;
    }

    public static String formatCurrency(double amount) {
        return String.format("Rs. %.2f", amount);
    }

    // File I/O for Products
    public static void saveProductsToCSV(List<Product> products, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("name,price,quantity");
            for (Product p : products) {
                writer.printf("%s,%.2f,%d%n", p.getName(), p.getPrice(), p.getQuantity());
            }
        }
    }

    public static List<Product> loadProductsFromCSV(String filename) throws IOException {
        List<Product> products = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    products.add(new Product(name, price, quantity));
                }
            }
        }
        return products;
    }

    // File I/O for Users
    public static void saveUsersToCSV(List<User> users, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("username,password,role");
            for (User u : users) {
                writer.printf("%s,%s,%s%n", u.getUsername(), u.getPassword(), u.getRole());
            }
        }
    }

    public static List<User> loadUsersFromCSV(String filename) throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String role = parts[2];
                    users.add(new User(username, password, role));
                }
            }
        }
        return users;
    }

    // File I/O for Sales
    public static void saveSalesToCSV(List<Sale> sales, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("saleId,customerType,customerName,totalAmount,discountAmount,finalAmount,paymentType,timestamp");
            for (Sale s : sales) {
                writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%s,%s%n",
                    s.getSaleId(),
                    s.getCustomer().getCustomerType(),
                    s.getCustomer().getName(),
                    s.getTotalAmount(),
                    s.getDiscountAmount(),
                    s.getFinalAmount(),
                    s.getPayment().getClass().getSimpleName(),
                    s.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        }
    }

    // Note: Loading sales is complex due to object recreation, so we'll implement basic version
    public static List<String> loadSaleStringsFromCSV(String filename) throws IOException {
        List<String> sales = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return sales;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                sales.add(line);
            }
        }
        return sales;
    }
}
