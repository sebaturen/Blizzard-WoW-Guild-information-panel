package com.blizzardPanel;

public class DataException extends Exception {
    private int errorCode;

    // Parameterless Constructor
    public DataException() {}

    // Constructor that accepts a message
    public DataException(String message)
    {
        super(message);
    }

    public void setErrorCode(int i) { this.errorCode = i; }
    public int getErrorCode() { return this.errorCode; }
}

