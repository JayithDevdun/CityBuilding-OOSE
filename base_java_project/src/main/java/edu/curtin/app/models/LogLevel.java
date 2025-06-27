package edu.curtin.app.models;

public enum LogLevel {
    /**Lowest level of logging used for debugging details */
    DEBUG(1, "DEBUG"),
    /**General information message*/
    INFORMATION(2, "INFORMATION"),
    /**Indications of potential issues or unusual situations*/
    WARNING(3, "WARNING"),
    /**Errors that may affect the program but allow it to continue running*/
    ERROR(4, "ERROR"),
    /**Critical issues that typically require immediate attention*/
    CRITICAL(5, "CRITICAL");
    
    private final int value;
    private final String description;
    
    LogLevel(int value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
}