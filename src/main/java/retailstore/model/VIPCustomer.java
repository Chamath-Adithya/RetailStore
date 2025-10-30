package model;

public class VIPCustomer extends Customer {
    public VIPCustomer(String name) {
        super(name);
    }

    @Override
    public double getDiscountRate() {
        return 0.1; // 10% discount
    }

    @Override
    public String getCustomerType() {
        return "VIP";
    }
}
