package gui;

import java.awt.*;
import javax.swing.*;

public class CustomerPanel extends JPanel {
    private JTextField txtCustomerName;
    private JComboBox<String> cmbCustomerType;

    public CustomerPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("Customer"));

        add(new JLabel("Name:"));
        txtCustomerName = new JTextField(10);
        add(txtCustomerName);

        add(new JLabel("Type:"));
        cmbCustomerType = new JComboBox<>(new String[]{"Regular", "VIP"});
        add(cmbCustomerType);
    }

    public String getCustomerName() {
        return txtCustomerName.getText();
    }

    public String getCustomerType() {
        return (String) cmbCustomerType.getSelectedItem();
    }
}
