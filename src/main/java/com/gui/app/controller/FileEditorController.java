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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.sql.SQLException;
public class FileEditorController {
    @FXML private TextField fileNameField;
    @FXML private TextArea fileContentArea;
    @FXML private Button saveButton;
    private static final Logger logger = LoggerUtil.getLogger(FileEditorController.class);
    private static final String BASE_URL = "http://localhost:8080";
    //private static final String BASE_URL = "http://host.docker.internal:8080";
    private String mode = "create";

    public void setMode(String mode, String filename, String content) {
        this.mode = mode;
        fileNameField.setText(filename);
        fileContentArea.setText(content);
        saveButton.setText(mode.equals("update") ? "Update File" : "Create File");
    }

    @FXML
    private void handleSave() throws IOException {
        String filename = fileNameField.getText();
        String content = fileContentArea.getText();

        if (filename.isEmpty()) {
            showAlert("File name is required.");
            return;
        }

        Path filePath = Paths.get("tempfiles", filename);
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
            logger.info("Created file: " + filename);
            
            if (mode.equals("create")) {
                   FileUtils.chunkAndEncryptAndUpload(filePath.toFile(), "", BASE_URL+"/upload");
             }
//            ChunkManager.splitAndDistribute(filePath.toFile());
            showAlert("File created and distributed successfully.");
        } catch (Exception e) {
            logger.severe("Error creating file: " + e.getMessage());
            showAlert("Failed to create file.");
        }
        

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "File saved.");
        alert.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
