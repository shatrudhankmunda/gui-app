module com.gui.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.gui.app to javafx.fxml;
    exports com.gui.app;
    opens com.gui.app.controller to javafx.fxml;  // 🔥 THIS is the key line
    exports com.gui.app.controller;

    opens com.gui.app.util to javafx.fxml;
    exports com.gui.app.util;
}
