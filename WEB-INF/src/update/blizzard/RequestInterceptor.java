package com.blizzardPanel.update.blizzard;

import com.blizzardPanel.Logs;
import okhttp3.Interceptor;
import okhttp3.Response;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class RequestInterceptor implements Interceptor {

    public static final int maxHourRequest = 36000;
    public static final int maxSecondRequest = 100;

    private static Date hourRequest = new Date();
    private static Date secondRequest = new Date();

    private static int requestCountSecond = 0;
    private static int requestCountHour = 0;

    @Override
    public Response intercept(Chain chain) throws IOException {

        // Add request count
        requestCountHour++;
        requestCountSecond++;

        // Check request per time
        Calendar evalDate = Calendar.getInstance();
        evalDate.add(Calendar.HOUR_OF_DAY, -1);
        if (evalDate.getTime().after(hourRequest)) { // check per hour
            if (requestCountHour >= maxHourRequest) {
                while (new Date().before(hourRequest)) {
                    Logs.infoLog(RequestInterceptor.class, "Waiting... maximum request per hour reached");
                    try {
                        Thread.sleep(600000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            requestCountHour = 0;
            hourRequest = new Date();
        }
        evalDate = Calendar.getInstance();
        evalDate.add(Calendar.SECOND, -1);
        if (evalDate.getTime().after(secondRequest)) { // check per second
            if (requestCountSecond >= maxSecondRequest) {
                while (new Date().before(secondRequest)) {
                    Logs.infoLog(RequestInterceptor.class, "Waiting... maximum request per second reached");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            requestCountSecond = 0;
            secondRequest = new Date();
        }
        return chain.proceed(chain.request());
    }

}
