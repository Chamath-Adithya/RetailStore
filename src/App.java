package retailstore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.*;
import payment.*;
import util.*;

public class App extends Application {
    private static User currentUser;
    private static List<Product> inventory = new ArrayList<>();
    private static List<Product> cart = new ArrayList<>();
    private static List<Sale> sales = new ArrayList<>();
    private static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadData();
        showLoginDialog(primaryStage);
    }

    private static void loadData() {
        try {
            inventory = StoreUtils.loadProductsFromCSV("data/products.csv");
            users = StoreUtils.loadUsersFromCSV("data/users.csv");
            // Load sales data - properly reconstruct Sale objects
            sales = loadSalesFromCSV("data/sales.csv");
        } catch (Exception e) {
            // Initialize with default data if files don't exist
            initializeDefaultData();
        }

        // If no data loaded, initialize defaults
        if (inventory.isEmpty() || users.isEmpty()) {
            initializeDefaultData();
        }
    }

    private static List<Sale> loadSalesFromCSV(String filename) {
        List<Sale> loadedSales = new ArrayList<>();
        try {
            List<String> saleStrings = StoreUtils.loadSaleStringsFromCSV(filename);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            for (String line : saleStrings) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String saleId = parts[0];
                    String customerType = parts[1];
                    String customerName = parts[2];
                    double totalAmount = Double.parseDouble(parts[3]);
                    double discountAmount = Double.parseDouble(parts[4]);
                    double finalAmount = Double.parseDouble(parts[5]);
                    String paymentType = parts[6];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[7], formatter);

                    // Create customer and payment objects
                    Customer customer = customerType.equals("VIP") ? new VIPCustomer(customerName) : new Customer(customerName);
                    Payment payment = paymentType.equals("CashPayment") ? new CashPayment() : new CardPayment();

                    // Use the constructor with pre-calculated amounts
                    Sale sale = new Sale(saleId, customer, payment, totalAmount, discountAmount, finalAmount, timestamp);
                    loadedSales.add(sale);
                }
            }
        } catch (Exception e) {
            // If loading fails, return empty list
            System.err.println("Warning: Could not load sales data: " + e.getMessage());
        }
        return loadedSales;
    }

    private static void initializeDefaultData() {
        try {
            // Create default data files if they don't exist
            java.io.File dataDir = new java.io.File("data");
            dataDir.mkdirs();

            java.io.File productsFile = new java.io.File("data/products.csv");
            if (!productsFile.exists()) {
                try (java.io.PrintWriter writer = new java.io.PrintWriter(productsFile)) {
                    writer.println("name,price,quantity");
                    writer.println("Apple,100.0,50");
                    writer.println("Banana,60.0,30");
                    writer.println("Orange,80.0,40");
                }
            }

            java.io.File usersFile = new java.io.File("data/users.csv");
            if (!usersFile.exists()) {
                try (java.io.PrintWriter writer = new java.io.PrintWriter(usersFile)) {
                    writer.println("username,password,role");
                    writer.println("admin,admin123,Admin");
                    writer.println("cashier,cash123,Cashier");
                }
            }

            // Now load the data
            inventory = StoreUtils.loadProductsFromCSV("data/products.csv");
            users = StoreUtils.loadUsersFromCSV("data/users.csv");

        } catch (Exception e) {
            // If all else fails, add minimal defaults to memory
            inventory.add(new Product("Apple", 100.0, 50));
            users.add(new User("admin", "admin123", "Admin"));
        }
    }

    private static void showLoginDialog(Stage primaryStage) {
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(30));

        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        Button btnLogin = new Button("Login");
        Button btnCancel = new Button("Cancel");

        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            for (User user : users) {
                if (user.getUsername().equals(username) && user.authenticate(password)) {
                    currentUser = user;
                    loginStage.close();
                    showMainWindow(primaryStage);
                    return;
                }
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid credentials!");
            alert.showAndWait();
        });

        btnCancel.setOnAction(e -> System.exit(0));

        grid.add(lblUsername, 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(lblPassword, 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(btnLogin, 1, 2);
        grid.add(btnCancel, 2, 2);

        Scene scene = new Scene(grid, 400, 200);
        loginStage.setScene(scene);
        loginStage.showAndWait();
    }

    private static void showMainWindow(Stage primaryStage) {
        primaryStage.setTitle("Retail Store Management - " + currentUser.getRole());

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save Data");
        saveItem.setOnAction(e -> saveData(primaryStage));
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(saveItem, exitItem);
        menuBar.getMenus().add(fileMenu);

        if (currentUser.isAdmin()) {
            Menu adminMenu = new Menu("Admin");
            MenuItem addProductItem = new MenuItem("Add Product");
            addProductItem.setOnAction(e -> showAddProductDialog(primaryStage));
            MenuItem removeProductItem = new MenuItem("Remove Product");
            removeProductItem.setOnAction(e -> removeSelectedProduct());
            MenuItem viewSalesItem = new MenuItem("View Sales History");
            viewSalesItem.setOnAction(e -> showSalesHistoryDialog(primaryStage));
            adminMenu.getItems().addAll(addProductItem, removeProductItem, new SeparatorMenuItem(), viewSalesItem);
            menuBar.getMenus().add(adminMenu);
        }

        // Main Layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        // Create observable lists for proper data binding
        ObservableList<Product> inventoryObservable = FXCollections.observableArrayList(inventory);
        ObservableList<Product> cartObservable = FXCollections.observableArrayList(cart);

        // Inventory Table
        TableView<Product> inventoryTable = new TableView<>();
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        inventoryTable.getColumns().addAll(nameCol, priceCol, stockCol);
        inventoryTable.setItems(inventoryObservable);

        Button btnAddToCart = new Button("Add to Cart");
        btnAddToCart.setOnAction(e -> addToCart(inventoryTable, inventoryObservable, cartObservable));

        VBox inventoryPanel = new VBox(10, new Label("Inventory"), inventoryTable, btnAddToCart);
        inventoryPanel.setPadding(new Insets(10));

        // Cart Table
        TableView<Product> cartTable = new TableView<>();
        TableColumn<Product, String> cartNameCol = new TableColumn<>("Name");
        cartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> cartPriceCol = new TableColumn<>("Price");
        cartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> cartQtyCol = new TableColumn<>("Quantity");
        cartQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartQtyCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        cartQtyCol.setOnEditCommit(event -> {
            Product item = event.getRowValue();
            int newQuantity = event.getNewValue();
            updateCartQuantity(item, newQuantity, cartTable, inventoryTable, cartObservable, inventoryObservable);
        });
        TableColumn<Product, Double> cartTotalCol = new TableColumn<>("Total");
        cartTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        cartTable.getColumns().addAll(cartNameCol, cartPriceCol, cartQtyCol, cartTotalCol);
        cartTable.setItems(cartObservable);
        cartTable.setEditable(true);

        Button btnRemoveFromCart = new Button("Remove from Cart");
        btnRemoveFromCart.setOnAction(e -> removeFromCart(cartTable, inventoryTable, cartObservable, inventoryObservable));

        VBox cartPanel = new VBox(10, new Label("Cart"), cartTable, btnRemoveFromCart);
        cartPanel.setPadding(new Insets(10));

        // Split Pane for Inventory and Cart
        SplitPane splitPane = new SplitPane(inventoryPanel, cartPanel);
        splitPane.setDividerPositions(0.5);

        // Bottom Panel for Checkout
        TextField txtCustomerName = new TextField("John Doe");
        ComboBox<String> cmbCustomerType = new ComboBox<>(FXCollections.observableArrayList("Regular", "VIP"));
        cmbCustomerType.setValue("Regular");
        ComboBox<String> cmbPaymentType = new ComboBox<>(FXCollections.observableArrayList("Cash", "Card"));
        cmbPaymentType.setValue("Cash");
        Button btnCheckout = new Button("Checkout");
        btnCheckout.setOnAction(e -> checkout(txtCustomerName.getText(),
                cmbCustomerType.getValue(),
                cmbPaymentType.getValue(), cartTable, inventoryTable, cartObservable, inventoryObservable));

        HBox bottomPanel = new HBox(10, new Label("Customer Name:"), txtCustomerName,
                cmbCustomerType, cmbPaymentType, btnCheckout);
        bottomPanel.setPadding(new Insets(10));

        root.setCenter(splitPane);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void updateCartQuantity(Product item, int newQuantity, TableView<Product> cartTable, TableView<Product> inventoryTable, ObservableList<Product> cartObservable, ObservableList<Product> inventoryObservable) {
        int oldQuantity = item.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;

        if (newQuantity > 0) {
            // Check if we have enough stock for the increase
            if (quantityDifference > 0) {
                // Find the product in inventory
                Product inventoryProduct = null;
                int inventoryIndex = -1;
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).getName().equals(item.getName())) {
                        inventoryProduct = inventory.get(i);
                        inventoryIndex = i;
                        break;
                    }
                }

                if (inventoryProduct != null && inventoryProduct.getQuantity() >= quantityDifference) {
                    // Update cart
                    Product updatedItem = new Product(item.getName(), item.getPrice(), newQuantity);
                    int cartIndex = cart.indexOf(item);
                    cart.set(cartIndex, updatedItem);
                    cartObservable.set(cartIndex, updatedItem);

                    // Update inventory
                    Product updatedInventory = new Product(inventoryProduct.getName(),
                        inventoryProduct.getPrice(),
                        inventoryProduct.getQuantity() - quantityDifference);
                    inventory.set(inventoryIndex, updatedInventory);
                    inventoryObservable.set(inventoryIndex, updatedInventory);
                } else {
                    // Not enough stock, reset to old quantity
                    cartTable.refresh();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Stock Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Not enough stock available!");
                    alert.showAndWait();
                }
            } else if (quantityDifference < 0) {
                // Quantity decreased, return stock to inventory
                Product updatedItem = new Product(item.getName(), item.getPrice(), newQuantity);
                int cartIndex = cart.indexOf(item);
                cart.set(cartIndex, updatedItem);
                cartObservable.set(cartIndex, updatedItem);

                // Return stock to inventory
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).getName().equals(item.getName())) {
                        Product inventoryProduct = inventory.get(i);
                        Product updatedInventory = new Product(inventoryProduct.getName(),
                            inventoryProduct.getPrice(),
                            inventoryProduct.getQuantity() - quantityDifference); // quantityDifference is negative, so this adds
                        inventory.set(i, updatedInventory);
                        inventoryObservable.set(i, updatedInventory);
                        break;
                    }
                }
            } else {
                // Quantity unchanged, just update cart
                Product updatedItem = new Product(item.getName(), item.getPrice(), newQuantity);
                int cartIndex = cart.indexOf(item);
                cart.set(cartIndex, updatedItem);
                cartObservable.set(cartIndex, updatedItem);
            }
        } else {
            // If invalid quantity, reset
            cartTable.refresh();
        }
    }

    private static void addToCart(TableView<Product> inventoryTable, ObservableList<Product> inventoryObservable, ObservableList<Product> cartObservable) {
        Product selectedProduct = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            if (selectedProduct.getQuantity() > 0) {
                // Simple add, in real app would handle quantity selection
                Product cartItem = new Product(selectedProduct.getName(), selectedProduct.getPrice(), 1);
                cart.add(cartItem);
                cartObservable.add(cartItem);
                int inventoryIndex = inventory.indexOf(selectedProduct);
                if (inventoryIndex >= 0) {
                    Product updatedProduct = new Product(selectedProduct.getName(), selectedProduct.getPrice(), selectedProduct.getQuantity() - 1);
                    inventory.set(inventoryIndex, updatedProduct);
                    inventoryObservable.set(inventoryIndex, updatedProduct);
                    System.out.println("Added " + cartItem.getName() + " to cart. Cart size: " + cart.size());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Out of Stock");
                alert.setHeaderText(null);
                alert.setContentText("Out of stock!");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a product from the inventory first!");
            alert.showAndWait();
        }
    }

    private static void removeFromCart(TableView<Product> cartTable, TableView<Product> inventoryTable, ObservableList<Product> cartObservable, ObservableList<Product> inventoryObservable) {
        Product selectedProduct = cartTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            cart.remove(selectedProduct);
            cartObservable.remove(selectedProduct);
            // Return to inventory
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).getName().equals(selectedProduct.getName())) {
                    Product p = inventory.get(i);
                    Product updated = new Product(p.getName(), p.getPrice(), p.getQuantity() + selectedProduct.getQuantity());
                    inventory.set(i, updated);
                    inventoryObservable.set(i, updated);
                    break;
                }
            }
        }
    }

    private static void checkout(String customerName, String customerType, String paymentType, TableView<Product> cartTable, TableView<Product> inventoryTable, ObservableList<Product> cartObservable, ObservableList<Product> inventoryObservable) {
        if (cart.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Cart");
            alert.setHeaderText(null);
            alert.setContentText("Cart is empty!");
            alert.showAndWait();
            return;
        }

        Customer customer = customerType.equals("VIP") ? new VIPCustomer(customerName) : new Customer(customerName);
        Payment payment = paymentType.equals("Cash") ? new CashPayment() : new CardPayment();

        String saleId = "TXN" + System.currentTimeMillis();
        Sale sale = new Sale(saleId, customer, cart, payment);
        sales.add(sale);

        // Save the sale immediately to persist data
        try {
            new java.io.File("data").mkdirs();
            StoreUtils.saveSalesToCSV(sales, "data/sales.csv");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Warning: Sale recorded but data save failed: " + e.getMessage());
            alert.showAndWait();
        }

        payment.pay(sale.getFinalAmount());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout Successful");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Checkout successful!\nTotal: Rs. %.2f\nDiscount: Rs. %.2f\nFinal: Rs. %.2f",
                sale.getTotalAmount(), sale.getDiscountAmount(), sale.getFinalAmount()));
        alert.showAndWait();

        cart.clear();
        cartObservable.clear();
    }

    private static void showAddProductDialog(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Product");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblName = new Label("Name:");
        TextField txtName = new TextField();
        Label lblPrice = new Label("Price:");
        TextField txtPrice = new TextField();
        Label lblQty = new Label("Quantity:");
        TextField txtQty = new TextField();
        Button btnAdd = new Button("Add");
        Button btnCancel = new Button("Cancel");

        btnAdd.setOnAction(e -> {
            try {
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText());
                int qty = Integer.parseInt(txtQty.getText());
                inventory.add(new Product(name, price, qty));
                dialog.close();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Invalid input!");
                alert.showAndWait();
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblPrice, 0, 1);
        grid.add(txtPrice, 1, 1);
        grid.add(lblQty, 0, 2);
        grid.add(txtQty, 1, 2);
        grid.add(btnAdd, 0, 3);
        grid.add(btnCancel, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static void removeSelectedProduct() {
        // For now, just remove the first item as an example
        // In a real app, you'd need to pass the table or have a way to select
        if (!inventory.isEmpty()) {
            inventory.remove(0);
        }
    }

    private static void saveData(Stage primaryStage) {
        try {
            new java.io.File("data").mkdirs();
            StoreUtils.saveProductsToCSV(inventory, "data/products.csv");
            StoreUtils.saveUsersToCSV(users, "data/users.csv");
            StoreUtils.saveSalesToCSV(sales, "data/sales.csv");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Successful");
            alert.setHeaderText(null);
            alert.setContentText("Data saved successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Error saving data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private static void showSalesHistoryDialog(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Sales History");

        // Create a simple table for sales
        TableView<Sale> salesTable = new TableView<>();
        TableColumn<Sale, String> saleIdCol = new TableColumn<>("Sale ID");
        saleIdCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        TableColumn<Sale, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomer().getName()));
        TableColumn<Sale, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomer().getCustomerType()));
        TableColumn<Sale, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        TableColumn<Sale, Double> discountCol = new TableColumn<>("Discount");
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        TableColumn<Sale, Double> finalCol = new TableColumn<>("Final");
        finalCol.setCellValueFactory(new PropertyValueFactory<>("finalAmount"));
        TableColumn<Sale, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPayment().getClass().getSimpleName()));
        TableColumn<Sale, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp().toString().replace("T", " ")));

        salesTable.getColumns().addAll(saleIdCol, customerCol, typeCol, totalCol, discountCol, finalCol, paymentCol, dateCol);
        salesTable.setItems(FXCollections.observableArrayList(sales));

        // Summary
        double totalRevenue = sales.stream().mapToDouble(Sale::getFinalAmount).sum();
        int totalSalesCount = sales.size();
        Label summaryLabel = new Label("Total Sales: " + totalSalesCount + " | Total Revenue: Rs. " + String.format("%.2f", totalRevenue));

        VBox layout = new VBox(10, salesTable, summaryLabel);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 800, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
