package com.gui.app.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {
    private static Stage mainStage;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      public static FXMLLoader loadScene(String title, String fxmlFile) {
         FXMLLoader loader = null;        
        try {
            loader = new FXMLLoader(SceneNavigator.class.getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            mainStage.setTitle(title);
            mainStage.setScene(scene);
            return loader;
        } catch (Exception e) {
            e.printStackTrace();
        }
    return loader;
    }
}
