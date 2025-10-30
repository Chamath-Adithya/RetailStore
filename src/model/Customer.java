package model;

public class Customer {
    protected String name;

    public Customer(String name) {
        this.name = name;
    }

    public double getDiscountRate() {
        return 0; // No discount for normal customer
    }

    public String getCustomerType() {
        return "Regular";
    }

    public String getName() {
        return name;
    }
}
