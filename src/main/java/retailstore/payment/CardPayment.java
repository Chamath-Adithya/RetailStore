package payment;

import model.Payment;

public class CardPayment extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid by Card: Rs. " + amount);
    }
}
