package edu.curtin.app.utilitites;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.curtin.app.models.LogLevel;

/**
 * A utility class for logging messages with different severity levels.
 * Supports logging to a file with configurable options and a singleton pattern for global access.
 */

public class Logger {
    /**
     * Default directory where log files will be stored*/
    private static final String LOG_DIRECTORY = "logs";

    /**
     * Default name of the log file*/
    private static final String LOG_FILE_NAME = "logger.log";

    /**
     * Minimum log level for messages to be written to the file (defaults to DEBUG)*/
    private LogLevel minFileLevel = LogLevel.DEBUG;

    /**
     * Directory where log files are stored (configurable)*/
    private String logDirectory;

    /**
     * Name of the log file (configurable)*/
    private String logFileName;

    /**
     * Flag to enable or disable logging to file (enabled by default)*/
    private boolean logToFile = true;

    // Date format for log timestamps (e.g., "2023-10-15 14:30:45")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Default constructor that initializes the logger with the default log directory and file name.
     */
    public Logger() {
        this(LOG_DIRECTORY, LOG_FILE_NAME);
    }

    /**
     * Constructor that allows specifying a custom log directory and file name.
     */
    public Logger(String directory, String fileName) {
        this.logDirectory = directory;
        this.logFileName = fileName;
        createLogDirectory();
    }

    /**
     * Creates the log directory if it does not exist and file logging is enabled.
     * Disables file logging if the directory cannot be created.
     */
    private void createLogDirectory() {
        if (logToFile) {
            File directory = new File(logDirectory);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    System.err.println("WARNING: Failed to create log directory: " + logDirectory);
                    logToFile = false;
                }
            }
        }
    }

    /**
     * Sets the minimum log level for messages to be written to the file.
     */
    public void setMinFileLevel(LogLevel level) {
        this.minFileLevel = level;
    }

    /**
     * Enables or disables logging to a file.
     * If enabled, attempts to create the log directory if it doesn't exist.
     */
    public void enableFileLogging(boolean enable) {
        this.logToFile = enable;
        if (enable) {
            createLogDirectory();
        }
    }

    /**
     * Sets the directory for storing log files and attempts to create it if necessary.
     */
    public void setLogDirectory(String directory) {
        this.logDirectory = directory;
        createLogDirectory();
    }

    /**
     * Sets the name of the log file.
     */
    public void setLogFileName(String fileName) {
        this.logFileName = fileName;
    }

    /**
     * Logs a debug-level message.
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /**
     * Logs an informational message.
     */
    public void info(String message) {
        log(LogLevel.INFORMATION, message);
    }

    /**
     * Logs a warning message.
     */
    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    /**
     * Logs an error message.
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs an error message with an associated exception.
     */
    public void error(String message, Exception e) {
        log(LogLevel.ERROR, message + ": " + e.getMessage());
    }

    /**
     * Logs a fatal (critical) error message.
     */
    public void fatal(String message) {
        log(LogLevel.CRITICAL, message);
    }

    /**
     * Logs a fatal (critical) error message with an associated exception.
     */
    public void fatal(String message, Exception e) {
        log(LogLevel.CRITICAL, message + ": " + e.getMessage());
    }

    /**
     * Logs a message with the specified log level.
     * Formats the message with a timestamp and log level description.
     */
    public void log(LogLevel level, String message) {
        String timestamp = dateFormat.format(new Date());
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level.getDescription(), message);

        /**
         * TODO: Add console logging functionality if needed (currently not implemented)*/

        /**
         * Write to file if file logging is enabled and the level meets the minimum threshold*/
        if (logToFile && level.getValue() >= minFileLevel.getValue()) {
            writeToLogFile(formattedMessage);
        }
    }

    /**
     * Writes a formatted log message to the log file.
     * Falls back to console output and disables file logging if an error occurs.
     */
    private void writeToLogFile(String message) {
        String logFilePath = logDirectory + File.separator + logFileName;

        try (FileWriter fw = new FileWriter(logFilePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (IOException e) {
            /**
             * Fallback to console if file writing fails*/
            System.err.println("ERROR: Failed to write to log file: " + e.getMessage());
            System.err.println("Original message: " + message);
            /**
             * Disable file logging to prevent repeated failures*/
            logToFile = false;
        }
    }

    /**
     * Singleton instance of the Logger class*/
    private static Logger instance;

    /**
     * Returns the singleton instance of the Logger.
     * Creates a new instance with default settings if none exists.
     */
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
}