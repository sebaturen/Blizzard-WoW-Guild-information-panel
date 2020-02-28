package com.blizzardPanel.blizzardAPI;

import java.util.Calendar;
import java.util.Date;

public class AccessToken {

    // Atribute
    private String access_token;
    private String token_type;
    private Date expire;

    public AccessToken() {
        this.expire = new Date();
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

    public long getExpire_in() {
        return expire_in;
    }

    public boolean isExpired() {
        Calendar c = Calendar.getInstance();
        // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date toDay = c.getTime();
        return !this.expire.before(toDay);
    }

    public void setExpire_in(long expire_in) {
        this.expire_in = expire_in;
    }
}
