package com.gui.app.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final StringProperty username;
    private final StringProperty role;
    public User(String username, String role) {
        this.username = new SimpleStringProperty(username);
        this.role = new SimpleStringProperty(role);
    }

    public String getUsername() { return username.get(); }
    public String getRole() { return role.get(); }

    public StringProperty usernameProperty() { return username; }
    public StringProperty roleProperty() { return role; }
}
