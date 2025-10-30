package gui;

import javax.swing.*;
import java.awt.*;
import model.Product;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private JButton btnAddToCart;

    public ProductPanel(List<Product> products) {
        initialize(products);
    }

    private void initialize(List<Product> products) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Products"));

        // Table for products
        String[] columns = {"Name", "Price", "Stock"};
        Object[][] data = new Object[products.size()][3];
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i][0] = p.getName();
            data[i][1] = p.getPrice();
            data[i][2] = p.getQuantity();
        }
        productTable = new JTable(data, columns);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        btnAddToCart = new JButton("Add to Cart");
        add(btnAddToCart, BorderLayout.SOUTH);
    }

    public JButton getAddToCartButton() {
        return btnAddToCart;
    }

    public JTable getProductTable() {
        return productTable;
    }
}
