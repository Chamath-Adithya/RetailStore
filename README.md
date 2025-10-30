# Retail Store Management System (GUI Edition)

A comprehensive Java Swing application demonstrating Object-Oriented Programming (OOP) concepts through a retail store management system with login, inventory management, and transaction processing.

## 🧱 Project Structure

```
RetailStoreGUI/
├── README.md
├── lib/                        # External libraries (if needed)
├── src/
│   ├── App.java                # Main entry point
│   ├── gui/                    # GUI classes (Swing forms, panels, frames)
│   │   ├── MainFrame.java
│   │   ├── ProductPanel.java
│   │   ├── CustomerPanel.java
│   │   └── BillingPanel.java
│   ├── model/                  # Business logic and OOP models
│   │   ├── Product.java
│   │   ├── Customer.java
│   │   ├── Sale.java
│   │   └── Payment.java
│   ├── service/                # Classes handling logic between model and GUI
│   │   ├── ProductService.java
│   │   ├── CustomerService.java
│   │   └── SalesService.java
│   └── util/                   # Helper functions (like file saving, validation)
│       ├── FileHandler.java
│       └── Validator.java
└── bin/                        # Compiled .class files
```

## 🧩 Object-Oriented Programming (OOP) Concepts Demonstrated

This project comprehensively demonstrates all four fundamental OOP principles through practical retail store management implementation:

### 📋 OOP Concepts Overview

| OOP Concept         | Implementation Location | Real-World Application |
|---------------------|------------------------|------------------------|
| **🔒 Encapsulation** | `Product`, `Customer`, `Sale`, `User` classes | Data protection and controlled access |
| **📈 Inheritance** | `Customer` → `VIPCustomer` hierarchy | Customer type specialization |
| **🎭 Polymorphism** | Method overriding in customer types & payment methods | Flexible behavior based on object types |
| **🎯 Abstraction** | `Payment` abstract class with concrete implementations | Payment method abstraction |

---

## 🔍 Detailed OOP Analysis

### 1️⃣ **Encapsulation** - Data Hiding & Access Control

**Implementation**: All model classes use private fields with controlled access through public methods.

```java
// Product.java - Encapsulation Example
public class Product {
    private String name;        // Private: Hidden from external access
    private double price;       // Private: Protected data integrity
    private int quantity;       // Private: Controlled stock management

    // Public getters: Controlled read access
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    // Constructor: Controlled object creation
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
```

**Benefits in Project**:
- **Data Integrity**: Prevents invalid stock quantities or negative prices
- **Controlled Access**: Business logic can validate data before storage
- **Maintainability**: Internal changes don't affect external code
- **Security**: Sensitive data (like pricing logic) remains hidden

---

### 2️⃣ **Inheritance** - Code Reuse & Hierarchical Relationships

**Implementation**: Customer hierarchy with base functionality inheritance.

```java
// Customer.java - Base Class
public class Customer {
    protected String name;  // Protected: Accessible to subclasses

    public Customer(String name) {
        this.name = name;
    }

    // Base discount implementation
    public double getDiscountRate() {
        return 0.0;  // Regular customers: 0% discount
    }

    public String getCustomerType() {
        return "Regular";
    }

    public String getName() {
        return name;
    }
}

// VIPCustomer.java - Derived Class
public class VIPCustomer extends Customer {
    public VIPCustomer(String name) {
        super(name);  // Call parent constructor
    }

    @Override
    public double getDiscountRate() {
        return 0.1;  // VIP customers: 10% discount
    }

    @Override
    public String getCustomerType() {
        return "VIP";  // Specialized behavior
    }
}
```

**Benefits in Project**:
- **Code Reuse**: VIPCustomer inherits all Customer functionality
- **Extensibility**: Easy to add new customer types (GoldCustomer, etc.)
- **Polymorphic Behavior**: Same method calls work differently
- **Hierarchical Design**: Clear customer type relationships

---

### 3️⃣ **Polymorphism** - Same Interface, Different Behaviors

**Implementation**: Method overriding and dynamic method resolution.

```java
// Polymorphic discount calculation
public double calculateDiscount(Customer customer) {
    return customer.getDiscountRate();  // Behaves differently based on actual type
}

// Usage examples:
Customer regular = new Customer("John");
Customer vip = new VIPCustomer("Jane");

calculateDiscount(regular);  // Returns 0.0 (0% discount)
calculateDiscount(vip);      // Returns 0.1 (10% discount)

// Both are Customer references but behave differently
```

**Payment Polymorphism**:
```java
// Payment abstraction with polymorphic behavior
Payment payment = paymentType.equals("Cash") ?
    new CashPayment() : new CardPayment();

payment.pay(amount);  // Same method, different implementations
```

**Benefits in Project**:
- **Flexible Checkout**: Same checkout code works for all customer types
- **Extensible Payments**: Easy to add new payment methods
- **Generic Processing**: Sales processing works regardless of customer/payment type
- **Runtime Flexibility**: Behavior determined by actual object type

---

### 4️⃣ **Abstraction** - Hiding Complexity, Showing Essentials

**Implementation**: Abstract Payment class with concrete implementations.

```java
// Payment.java - Abstract Class
public abstract class Payment {
    // Abstract method: Contract that must be implemented
    public abstract void pay(double amount);
}

// CashPayment.java - Concrete Implementation
public class CashPayment extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid in Cash: Rs. " + amount);
        // Cash-specific payment logic
    }
}

// CardPayment.java - Another Concrete Implementation
public class CardPayment extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid by Card: Rs. " + amount);
        // Card-specific payment logic (validation, processing, etc.)
    }
}
```

**Usage in Project**:
```java
// Abstraction in action - client code doesn't know implementation details
Payment payment = createPayment(paymentType);  // Factory method
payment.pay(finalAmount);  // Works regardless of payment type
```

**Benefits in Project**:
- **Simplified Interface**: Checkout code doesn't need payment details
- **Extensibility**: Add UPI, Wallet payments without changing checkout logic
- **Modularity**: Payment logic separated from business logic
- **Testability**: Easy to mock payment behavior for testing

---

## 🏗️ Advanced OOP Patterns in Project

### **Factory Pattern** (Payment Creation)
```java
// Service layer creates appropriate payment objects
Payment payment = paymentType.equals("Cash") ?
    new CashPayment() : new CardPayment();
```

### **Strategy Pattern** (Discount Strategies)
```java
// Different discount strategies based on customer type
double discount = customer.getDiscountRate() * totalAmount;
```

### **Observer Pattern** (Table Model Listeners)
```java
// Cart table notifies listeners of quantity changes
cartTable.getModel().addTableModelListener(e -> {
    // Automatically update totals and inventory
});
```

### **Composition over Inheritance**
```java
// Sale class composes Customer, Payment, and Product list
public class Sale {
    private Customer customer;      // HAS-A relationship
    private Payment payment;        // HAS-A relationship
    private List<Product> products; // HAS-A relationship
}
```

---

## 🎯 OOP Principles in Action

### **Single Responsibility Principle (SRP)**
- `ProductService`: Only handles product operations
- `CustomerService`: Only manages customer data
- `SalesService`: Only processes sales transactions

### **Open/Closed Principle (OCP)**
- Easy to add new customer types without modifying existing code
- New payment methods can be added without changing checkout logic

### **Liskov Substitution Principle (LSP)**
- Any `Customer` subclass can be used wherever `Customer` is expected
- Any `Payment` subclass works with payment processing code

### **Dependency Inversion Principle (DIP)**
- High-level modules (App) don't depend on low-level modules (specific payment types)
- Both depend on abstractions (Payment interface)

---

## 📊 OOP Impact on Project Quality

| Quality Attribute | OOP Contribution |
|------------------|------------------|
| **Maintainability** | Encapsulated changes don't ripple through codebase |
| **Extensibility** | New features added without modifying existing code |
| **Reusability** | Classes can be reused in different contexts |
| **Testability** | Individual components can be unit tested |
| **Readability** | Code structure reflects real-world relationships |
| **Reliability** | Type safety and encapsulation prevent bugs |

## 🏃 How to Run

In VS Code terminal:

```bash
javac -d bin src/App.java src/model/*.java src/payment/*.java src/util/*.java src/service/*.java src/gui/*.java
cd bin
java App
```

## 🎯 Features

- **User Authentication**: Login system with Admin and Cashier roles
- **Inventory Management**: JTable display of products with stock levels
- **Shopping Cart**: Add/remove products to/from cart with JTable display
- **Stock Management**: Real-time stock updates, add/remove products (Admin only)
- **Customer Types**: Regular customers (0% discount) and VIP customers (10% discount)
- **Payment Methods**: Cash or Card payment processing with transaction records
- **Data Persistence**: CSV file storage for products, users, and transactions
- **GUI Interface**: Comprehensive Swing interface with tables, menus, and dialogs
- **Transaction Processing**: Complete checkout with discount calculation and payment
- **Role-based Access**: Different features available based on user role

### Additional OOP Concepts in New Features:

#### 5️⃣ **Composition** (Sale.java)
```java
public class Sale {
    private Customer customer;
    private List<Product> products;
    private Payment payment;
    // ... contains multiple objects working together
}
```
- **Purpose**: Build complex objects from simpler ones
- **Benefits**: Flexible relationships between classes

#### 6️⃣ **Polymorphism in Collections** (StoreUtils.java)
```java
public static void saveProductsToCSV(List<Product> products, String filename)
public static void saveUsersToCSV(List<User> users, String filename)
```
- **Purpose**: Generic methods that work with different types
- **Benefits**: Code reuse and type safety

## 📚 Learning Objectives

This project teaches:
- How to structure Java projects with packages
- Implementing OOP principles in real applications
- Creating GUI applications with Swing
- Separating concerns (model, view, payment logic)
- Writing maintainable and extensible code
