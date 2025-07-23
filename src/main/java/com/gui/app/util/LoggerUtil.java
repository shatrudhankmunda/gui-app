package com.gui.app.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

public class LoggerUtil {
    private static FileHandler fileHandler;
    private static final Set<String> initializedLoggers = new HashSet<>();

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());

        // Prevent duplicate handlers for same logger
        if (!initializedLoggers.contains(clazz.getName())) {
            try {
                // Ensure logs/ directory exists
                File logDir = new File("logs");
                if (!logDir.exists()) {
                    logDir.mkdirs();
                }

                if (fileHandler == null) {
                    fileHandler = new FileHandler("logs/app.log", true);
                    fileHandler.setFormatter(new SimpleFormatter());
                }

                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(true); // keep console output
                logger.setLevel(Level.ALL);

                initializedLoggers.add(clazz.getName());
            } catch (IOException e) {
                System.err.println("Logger setup failed for " + clazz.getName() + ": " + e.getMessage());
            }
        }

        return logger;
    }
}
