/**
 * File : ConfigurationException.java
 * Desc : Control configuration a sistem Exceptions
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.exceptions;

public class ConfigurationException extends Exception
{
    
    // Parameterless Constructor
    public ConfigurationException() {}

    // Constructor that accepts a message
    public ConfigurationException(String message)
    {
        super(message);
    }
    
}