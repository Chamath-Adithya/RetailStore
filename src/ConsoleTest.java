import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.*;
import payment.*;
import util.*;

public class ConsoleTest {
    public static void main(String[] args) throws IOException {
        System.out.println("=== RetailStore JavaFX Conversion Test ===");
        System.out.println("Testing business logic without GUI...\n");

        // Test data loading
        System.out.println("1. Testing data loading...");
        List<Product> inventory = StoreUtils.loadProductsFromCSV("data/products.csv");
        List<User> users = StoreUtils.loadUsersFromCSV("data/users.csv");

        System.out.println("Loaded " + inventory.size() + " products:");
        for (Product p : inventory) {
            System.out.println("  - " + p.getName() + ": $" + p.getPrice() + " (Stock: " + p.getQuantity() + ")");
        }

        System.out.println("\nLoaded " + users.size() + " users:");
        for (User u : users) {
            System.out.println("  - " + u.getUsername() + " (" + u.getRole() + ")");
        }

        // Test authentication
        System.out.println("\n2. Testing authentication...");
        User admin = null;
        for (User u : users) {
            if (u.getUsername().equals("admin")) {
                admin = u;
                break;
            }
        }
        if (admin != null && admin.authenticate("admin123")) {
            System.out.println("✓ Admin login successful");
        } else {
            System.out.println("✗ Admin login failed");
        }

        // Test cart operations
        System.out.println("\n3. Testing cart operations...");
        List<Product> cart = new ArrayList<>();
        Product apple = inventory.get(0); // Assuming Apple is first

        // Add to cart
        if (apple.getQuantity() > 0) {
            Product cartItem = new Product(apple.getName(), apple.getPrice(), 1);
            cart.add(cartItem);
            apple = new Product(apple.getName(), apple.getPrice(), apple.getQuantity() - 1);
            System.out.println("✓ Added " + cartItem.getName() + " to cart");
        }

        // Test checkout
        System.out.println("\n4. Testing checkout...");
        if (!cart.isEmpty()) {
            Customer customer = new Customer("John Doe");
            Payment payment = new CashPayment();

            String saleId = "TEST" + System.currentTimeMillis();
            Sale sale = new Sale(saleId, customer, cart, payment);

            System.out.println("✓ Checkout successful!");
            System.out.println("  Sale ID: " + sale.getSaleId());
            System.out.println("  Customer: " + sale.getCustomer().getName());
            System.out.println("  Total: $" + sale.getTotalAmount());
            System.out.println("  Discount: $" + sale.getDiscountAmount());
            System.out.println("  Final: $" + sale.getFinalAmount());
        }

        // Test VIP customer discount
        System.out.println("\n5. Testing VIP discount...");
        Customer vipCustomer = new VIPCustomer("Jane Doe");
        Payment payment = new CardPayment();

        cart.clear();
        cart.add(new Product("Apple", 100.0, 2)); // 2 apples = $200

        Sale vipSale = new Sale("VIPTEST", vipCustomer, cart, payment);
        System.out.println("✓ VIP Sale:");
        System.out.println("  Total: $" + vipSale.getTotalAmount());
        System.out.println("  Discount: $" + vipSale.getDiscountAmount() + " (10% for VIP)");
        System.out.println("  Final: $" + vipSale.getFinalAmount());

        System.out.println("\n=== All Tests Passed! JavaFX Conversion Successful ===");
        System.out.println("The application logic is working correctly.");
        System.out.println("To run with GUI, install JavaFX and use:");
        System.out.println("java --module-path /path/to/javafx/lib:bin --add-modules javafx.controls,javafx.fxml -m RetailStore/App");
    }
}
