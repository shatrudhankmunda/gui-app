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

import java.sql.SQLException;
public class SharePanelController {
    @FXML private TextField fileField, sharedUserField;
    @FXML private CheckBox readCheck, writeCheck;

    @FXML
    private void handleGrantAccess() {

        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();
        AclEntry entry = new AclEntry();
        entry.setFilename(fileField.getText());
        entry.setOwner(user.getUsername());
        entry.setSharedUser(sharedUserField.getText());
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
