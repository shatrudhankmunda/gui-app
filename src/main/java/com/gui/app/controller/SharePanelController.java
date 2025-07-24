package com.gui.app.controller;
import com.gui.app.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;
import com.gui.app.util.*;
import com.gui.app.session.*;
import com.gui.app.model.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;
public class SharePanelController {
    @FXML private TextField fileField;
     @FXML private CheckComboBox<String> userCheckCombo;
    @FXML private CheckBox readCheck, writeCheck;
    
   @FXML
    public void initialize() {
        ObservableList<String> users = FXCollections.observableList(SQLiteHelper.getAllUsername());
        userCheckCombo.getItems().setAll(users);
    }
    

    public void setMode(String filename) {
        fileField.setText(filename);
    }
    @FXML
    private void handleGrantAccess() {
        ObservableList<String> selected = userCheckCombo.getCheckModel().getCheckedItems();
        if (selected.isEmpty()) {
            showAlert("Please select a username.");
            return;
        }
        for(String targetUser : selected){
         
        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();
        AclEntry entry = new AclEntry();
        entry.setFilename(fileField.getText());
        entry.setOwner(user.getUsername());
        entry.setSharedUser(targetUser);
        entry.setCanRead(readCheck.isSelected());
        entry.setCanWrite(writeCheck.isSelected());

        try {
            AclManager.grantAccess(entry);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Access granted.");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }

    }
 private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
