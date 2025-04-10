package ca.proj.Utility;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger("HotelReservationSystem");
    private static final String LOG_FILE = "system_logs.log";
    private static boolean isInitialized = false;

    static {
        initializeLogger();
    }

    private static void initializeLogger() {
        if (!isInitialized) {
            try {
                FileHandler fileHandler = new FileHandler(LOG_FILE, 1024 * 1024, 1, false); // 1MB, 10 files, append
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(false); // Disable console output, log to file only
                logger.info("Logger initialized");
                isInitialized = true;
            } catch (IOException e) {
                System.err.println("Failed to initialize logger: " + e.getMessage());
            }
        }
    }

    public static Logger getLogger() {
        return logger;
    }

}
