package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private String saleId;
    private Customer customer;
    private List<Product> products;
    private Payment payment;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private LocalDateTime timestamp;

    public Sale(String saleId, Customer customer, List<Product> products, Payment payment) {
        this.saleId = saleId;
        this.customer = customer;
        this.products = new ArrayList<>(products);
        this.payment = payment;
        this.timestamp = LocalDateTime.now();
        calculateAmounts();
    }

    // Constructor for loading from CSV with pre-calculated amounts
    public Sale(String saleId, Customer customer, Payment payment,
                double totalAmount, double discountAmount, double finalAmount, LocalDateTime timestamp) {
        this.saleId = saleId;
        this.customer = customer;
        this.products = new ArrayList<>(); // Empty for loaded sales
        this.payment = payment;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.timestamp = timestamp;
    }

    private void calculateAmounts() {
        this.totalAmount = products.stream().mapToDouble(Product::getTotal).sum();
        this.discountAmount = totalAmount * customer.getDiscountRate();
        this.finalAmount = totalAmount - discountAmount;
    }

    // Getters
    public String getSaleId() { return saleId; }
    public Customer getCustomer() { return customer; }
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public Payment getPayment() { return payment; }
    public double getTotalAmount() { return totalAmount; }
    public double getDiscountAmount() { return discountAmount; }
    public double getFinalAmount() { return finalAmount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Sale ID: %s, Customer: %s, Total: Rs. %.2f, Final: Rs. %.2f",
                saleId, customer.getCustomerType(), totalAmount, finalAmount);
    }
}
