package com.example.seemore;

import android.content.Context;
import android.widget.Toast
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class events {
	//URL for Eventbrite API to get events happening in the city
    private static String BASE_URL = "https://www.eventbriteapi.com/v3/events/search/?location.latitude=";
    private static String BACK_URL = "T23%3A59%3A59Z&token=KUWBRV7DYYQS7FD7W7RF";
	
    public String getEventData(Context context,String lon, String lat) {
        HttpURLConnection con = null ;
        InputStream is = null;
        String Datetime;
        Date m = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(m);
        cal.add(Calendar.DATE, 14); // 14 is the days to add or subtract
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Datetime = dateformat.format(cal.getTime());
        try {
            con = (HttpURLConnection) ( new URL(BASE_URL+lat +"&location.longitude="+lon+"&start_date.range_end="+Datetime+BACK_URL)).openConnection();
            con.setRequestMethod("GET");
            con.connect();
            // Read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }
}
