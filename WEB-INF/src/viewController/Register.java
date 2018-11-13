/**
 * File : Register.java
 * Desc : Register.jsp login controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.dbConnect.DBStructure;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register 
{    
    private final DBConnect dbConnect;
    
    private String email;
    private String password;
    private boolean isDuplicateUser = false;
    private boolean tryRegist = false;
    public static final String emptyHashMD5 = "d41d8cd98f00b204e9800998ecf8427e";
    
    public Register()
    {
        this.dbConnect = new DBConnect();
    }    
    
    public boolean saveRegister()
    {
        if(this.email == null || this.password == null || 
           this.email.isEmpty() || this.password.equals("emptyHashMD5")) 
            return false;
        try {      
            this.tryRegist = true;
            if (!emailCheck(this.email)) return false;
            //{"email", "password", "battle_tag", "access_code"};
            dbConnect.insert(DBStructure.USER_TABLE_NAME,
                            DBStructure.USER_TABLE_KEY,
                           new String[] {"email", "password"},
                           new String[] {this.email, this.password});
            return true;
        } catch (DataException e) {
            System.out.println("Fail to save user info..."+ this.email +" - "+ e.getErrorCode() +" - "+ e);
            if(e.getErrorCode() == DBConnect.ERROR_DUPLICATE_KEY)isDuplicateUser = true;
        } catch (ClassNotFoundException ex) {
            System.out.println("Fail to save user info..."+ this.email +" - "+ ex);
        }
        return false;
    }
    
    /* This function is move to JQuery in browser info! not send from server!
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
    }*/
    
    public static boolean emailCheck(String email) {        
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);        
        return matcher.matches();
    }
    
    //Getters and Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public boolean isData() { return !(this.email == null || this.password == null); }
    public boolean isDuplicateUser() { return this.isDuplicateUser; }
    public boolean isTryRegist() { return this.tryRegist; }
    
    
}
