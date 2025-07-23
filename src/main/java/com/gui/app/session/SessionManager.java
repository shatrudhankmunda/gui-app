package com.gui.app.session;
import com.gui.app.model.User;
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private long lastActivityTime;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        updateActivity();
    }

    public void logout() {
        currentUser = null;
        lastActivityTime = 0;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateActivity() {
        lastActivityTime = System.currentTimeMillis();
    }

    public boolean isSessionActive(long timeoutMillis) {
        return (System.currentTimeMillis() - lastActivityTime) < timeoutMillis;
    }
}
