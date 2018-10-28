/**
 * File : DataException.java
 * Desc : Control a sistem Exceptions
 * @author Sebastián Turen Croquevielle(seba@turensoft.com)
 */
package com.artOfWar;

public class DataException extends Exception
{
	  // Parameterless Constructor
      public DataException() {}

      // Constructor that accepts a message
      public DataException(String message)
      {
         super(message);
      }	
	
} 

