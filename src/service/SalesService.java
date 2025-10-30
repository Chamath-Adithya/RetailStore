package service;

import model.Sale;
import model.Customer;
import model.Product;
import model.Payment;
import payment.CashPayment;
import payment.CardPayment;
import java.util.List;
import java.util.ArrayList;

public class SalesService {
    private List<Sale> sales;

    public SalesService() {
        this.sales = new ArrayList<>();
    }

    public void addSale(Sale sale) {
        sales.add(sale);
    }

    public List<Sale> getAllSales() {
        return new ArrayList<>(sales);
    }

    public Sale createSale(String saleId, Customer customer, List<Product> products, String paymentType) {
        Payment payment = "Cash".equals(paymentType) ? new CashPayment() : new CardPayment();
        return new Sale(saleId, customer, products, payment);
    }

    public double getTotalRevenue() {
        return sales.stream().mapToDouble(Sale::getFinalAmount).sum();
    }

    public int getTotalSalesCount() {
        return sales.size();
    }
}
