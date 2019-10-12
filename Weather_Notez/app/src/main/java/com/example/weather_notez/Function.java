package com.example.weather_notez;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

public class Function {

/**
 * Checks to see if we are able to connect to the internet
 * @param context
 * */
    public static boolean isNetworkAvalable(Context context) {

        return ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;


    }

    /**
     * Reading the online file*/
    public static String executeget(String targeturl){

        URL url;
        try {
            url = new URL(targeturl);

            /**
             * thus converting the stream of data into a single solid value. So when you are scanning values using BufferedReader,
             * what it does is that it first stores the value in a buffer, then using the readLine() method it takes the entire value from
             * the buffer and stores in the variable. The readLine() method stores the value as soon as we hit the Enter key from our keyboard.
             *  The InputStreamReader stores the incoming stream of data into the buffer and then using BufferedReader it is stored into the variable.
             * */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));


            //similar to Stringbuffer but synchronized
            StringBuffer content = new StringBuffer();

            String line;
            //read the content of the file using the readLine method:
            while((line = bufferedReader.readLine())!= null){

                //Add's the content of the file being read into line
                //adds it to var:content
                content.append(line);
                //adds a new line to seperate and make more readable
                content.append('\n');

            }

            bufferedReader.close();

            return content.toString();
        }catch(Exception ex ){
            ex.printStackTrace();
            return null;
        }
    }

    public static String setWeatherIcon(int actualId){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            //5 is the time the sun rises(guessing) ~5am
            //19 is the time the sunsets also guessing ~7pm
            if(currentTime>=5 && currentTime<19) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }



}
