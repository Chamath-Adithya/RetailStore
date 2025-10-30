package gui;

import javax.swing.*;
import java.awt.*;
import model.User;

public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        initialize();
    }

    private void initialize() {
        setTitle("Retail Store Management - " + currentUser.getRole());
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components here
        // For now, just a placeholder
        add(new JLabel("Main Frame for " + currentUser.getRole()), BorderLayout.CENTER);

        setVisible(true);
    }
}
