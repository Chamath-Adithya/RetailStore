package payment;

import model.Payment;

public class CashPayment extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid in Cash: Rs. " + amount);
    }
}
