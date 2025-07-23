//package com.gui.app.util;
//
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.util.Duration;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.input.KeyEvent;
//
//public class SceneNavigator2 {
//    public static Stage mainStage;
//    private static Timeline timeoutChecker;
//
//    public static void setMainStage(Stage stage) {
//        mainStage = stage;
//        startSessionMonitor();
//    }
//
//    public static void loadScene(String fxmlFile) {
//        try {
//            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource("/fxml/" + fxmlFile));
//            Parent root = loader.load();
//
//            Scene scene = new Scene(root);
//            scene.addEventFilter(MouseEvent.ANY, e -> SessionManager.getInstance().refreshInteractionTime());
//            scene.addEventFilter(KeyEvent.ANY, e -> SessionManager.getInstance().refreshInteractionTime());
//
//            mainStage.setScene(scene);
//            mainStage.setTitle(fxmlFile.replace(".fxml", "")); // Optional: customize title
//            mainStage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void startSessionMonitor() {
//        timeoutChecker = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
//            SessionManager session = SessionManager.getInstance();
//            if (session.isLoggedIn() && session.isSessionExpired()) {
//                session.logout();
//                loadScene("Login.fxml");
//            }
//        }));
//        timeoutChecker.setCycleCount(Timeline.INDEFINITE);
//        timeoutChecker.play();
//    }
//}
