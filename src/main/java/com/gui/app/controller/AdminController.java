package com.gui.app.controller;


import com.gui.app.model.User;
import com.gui.app.util.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private ObservableList<User> users = FXCollections.observableArrayList();

    public void loadUserList() {
        users.setAll(UserManager.getAllUsers());
        usernameColumn.setCellValueFactory(data -> data.getValue().usernameProperty());
        roleColumn.setCellValueFactory(data -> data.getValue().roleProperty());
        userTable.setItems(users);
    }

    @FXML
    private void handlePromote() {
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user != null && !user.getRole().equals("admin")) {
            UserManager.updateUserRole(user.getUsername(), "admin");
            loadUserList();
        }
    }

    @FXML
    private void handleDemote() {
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user != null && !user.getRole().equals("standard")) {
            UserManager.updateUserRole(user.getUsername(), "standard");
            loadUserList();
        }
    }

    @FXML
    private void handleDelete() {
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user != null) {
            UserManager.deleteUser(user.getUsername());
            loadUserList();
        }
    }
}
