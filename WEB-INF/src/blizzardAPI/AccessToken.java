package com.blizzardPanel.blizzardAPI;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class AccessToken {

    // Atribute
    private String access_token;
    private String token_type;
    private double expire_in;
    private Date expire;

    public AccessToken() {

    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public double getExpire_in() {
        return expire_in;
    }

    public boolean isExpired() {
        Date today = new Date();
        return !expire.after(today);
    }

    public void setExpire_in(double expire_in) {
        long currentTime = Instant.now().getEpochSecond();
        this.expire_in = expire_in;
        long expireTime = currentTime + (long) this.expire_in;
        this.expire = new Date(expireTime*1000);

    }

    public String getAuthorization() {
        return getToken_type() +" "+ getAccess_token();
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expire_in=" + expire_in +
                ", expire=" + expire +
                '}';
    }
}
