package gui;

import java.awt.*;
import javax.swing.*;

public class BillingPanel extends JPanel {
    private JComboBox<String> cmbPaymentType;
    private JButton btnCheckout;

    public BillingPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("Billing"));

        add(new JLabel("Payment:"));
        cmbPaymentType = new JComboBox<>(new String[]{"Cash", "Card"});
        add(cmbPaymentType);

        btnCheckout = new JButton("Checkout");
        add(btnCheckout);
    }

    public String getPaymentType() {
        return (String) cmbPaymentType.getSelectedItem();
    }

    public JButton getCheckoutButton() {
        return btnCheckout;
    }
}
