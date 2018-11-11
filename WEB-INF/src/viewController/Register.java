/**
 * File : Register.java
 * Desc : Register.jsp login controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.dbConnect.DBStructure;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Register 
{    
    private final DBConnect dbConnect;
    
    private String email;
    private String password;
    
    public Register()
    {
        this.dbConnect = new DBConnect();
    }    
    
    public boolean saveRegister()
    {
        if(this.email == null || this.password == null) return false;
        try {      
            //{"email", "password", "battle_tag", "access_code"};
            dbConnect.insert(DBStructure.USER_TABLE_NAME,
                            DBStructure.USER_TABLE_KEY,
                           new String[] {"email", "password"},
                           new String[] {this.email, encodePass(this.password)});
            return true;
        } catch (ClassNotFoundException|DataException ex) {
            System.out.println("Fail to save user info..."+ this.email +" - "+ ex);
        }
        return false;
    }
    
    public static String encodePass(String password)
    {
        String digest = null; 
        try { 
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes("UTF-8")); 
            //converting byte array to Hexadecimal String 
            StringBuilder sb = new StringBuilder(2*hash.length); 
            for(byte b : hash)
            { 
                sb.append(String.format("%02x", b&0xff)); 
            }
            digest = sb.toString(); 
        } catch (UnsupportedEncodingException|NoSuchAlgorithmException ex) {
            System.out.println("Fail to convert password "+ ex);
        }
        return digest;
    }
    
    //Getters and Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public boolean isData() { return !(this.email == null || this.password == null); }
    
    
}
