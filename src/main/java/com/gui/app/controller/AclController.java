package com.gui.app.controller;


import com.gui.app.util.AclManager;
import com.gui.app.model.*;
import com.gui.app.session.SessionManager;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.*;

import java.sql.SQLException;

public class AclController {
    @FXML private TextField fileField;
    @FXML private TextField sharedUserField;
    @FXML private CheckBox readCheck, writeCheck;
    @FXML private TableView<AclEntry> aclTable;
    @FXML private TableColumn<AclEntry, String> userCol;
    @FXML private TableColumn<AclEntry, Boolean> readCol, writeCol;
    @FXML private TableColumn<AclEntry, Void> actionCol;

    @FXML
    public void initialize() {
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSharedUser()));
        readCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isCanRead()));
        writeCol.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isCanWrite()));

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button revokeBtn = new Button("Revoke");

            {
                revokeBtn.setOnAction(e -> {
                    AclEntry entry = getTableView().getItems().get(getIndex());
                    try {
                        AclManager.revokeAccess(entry.getFilename(), entry.getSharedUser());
                        handleRefresh();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(revokeBtn);
            }
        });
    }

    @FXML
    private void handleGrant() {
        String filename = fileField.getText();
        String targetUser = sharedUserField.getText();
        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();
        AclEntry entry = new AclEntry();
        entry.setFilename(filename);
        entry.setOwner(user.getUsername());
        entry.setSharedUser(targetUser);
        entry.setCanRead(readCheck.isSelected());
        entry.setCanWrite(writeCheck.isSelected());

        try {
            AclManager.grantAccess(entry);
            handleRefresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        String filename = fileField.getText();
        try {
            ObservableList<AclEntry> entries = FXCollections.observableArrayList(
                AclManager.getPermissions(filename)
            );
            aclTable.setItems(entries);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
