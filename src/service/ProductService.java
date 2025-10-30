package service;

import model.Product;
import java.util.List;
import java.util.ArrayList;

public class ProductService {
    private List<Product> products;

    public ProductService() {
        this.products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product findProductByName(String name) {
        for (Product p : products) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void updateProductStock(String name, int newQuantity) {
        Product p = findProductByName(name);
        if (p != null) {
            // Since Product is immutable, we need to replace it
            products.remove(p);
            products.add(new Product(p.getName(), p.getPrice(), newQuantity));
        }
    }
}
