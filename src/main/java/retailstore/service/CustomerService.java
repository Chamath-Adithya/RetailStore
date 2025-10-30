package service;

import model.Customer;
import model.VIPCustomer;
import java.util.List;
import java.util.ArrayList;

public class CustomerService {
    private List<Customer> customers;

    public CustomerService() {
        this.customers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Customer createCustomer(String name, String type) {
        if ("VIP".equals(type)) {
            return new VIPCustomer(name);
        } else {
            return new Customer(name);
        }
    }
}
