package com.example.weather_notez;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
/**Icons Used:http://erikflowers.github.io/weather-icons/
 * Based off :https://androstock.com/tutorials/create-a-weather-app-on-android-android-studio.html
 * used:https://openweathermap.org/city/5368361, for weather
 * Copy paste following to browser to see json;
 * http://api.openweathermap.org/data/2.5/forecast?q=Los%20Angeles&units=imperial&APPID=26757496e47d1fb956b0e1040b880bcd
 * */

    private ConstraintLayout app_background;
    private ProgressBar loader;
    private TextView weathericon,city_label,current_temp,description,humidity_label;
    private String apiKey = "26757496e47d1fb956b0e1040b880bcd";

    private String city = "Los%20Angeles";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Hides title bar
        getSupportActionBar().hide();

        //i gave the application and id in the main.xml as 'background'
        app_background = findViewById(R.id.background);
        loader = findViewById(R.id.loader);
        weathericon = findViewById(R.id.weather_icon);
        city_label = findViewById(R.id.city_name);
        current_temp = findViewById(R.id.temp);
        description = findViewById(R.id.description);
        humidity_label = findViewById(R.id.humidity);

        Typeface weatherfont = Typeface.createFromAsset(getAssets(), "Fonts/weathericons-regular-webfont.ttf");
        //Gets the our custom font and assigns it to weather icon
        //in other words create from assets says we have this custom font
        //using set type face we are asigning that custom font to this text view
        weathericon.setTypeface(weatherfont);



        taskLoadUp(city);
    }//OnCreate()

    // onClickListener for Notes Button and the actual Button
    public void open_notes(View view) {
        Toast.makeText(MainActivity.this,R.string.loading_notes,Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, notes.class);

        startActivity(intent);
    }

    public void taskLoadUp(String city){

        if(Function.isNetworkAvalable(getApplicationContext())){
            DownloadWeather task = new DownloadWeather();
            task.execute(city);

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.no_Internet, Toast.LENGTH_LONG).show();
        }
    }



    /**
     * the three parameters are for doinBackground,onPreExecute, and onPostExecute, respectively:
     * <params,progress,result>
     * Params, the type of the parameters sent to the task upon execution.
     * Progress, the type of the progress units published during the background computation.
     * Result, the type of the result of the background computation.
     * */
    private class DownloadWeather extends AsyncTask<String,Void,String>{

        /**
         * Before anything show the progress bar to show client we are loading the stuff
         * up*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... string) {
            Log.d("mk", "doInBackground: " + string[0]);
            //http://api.openweathermap.org/data/2.5/forecast?q=Los%20Angeles&units=imperial&APPID=26757496e47d1fb956b0e1040b880bcd

            String xml = Function.executeget("https://api.openweathermap.org/data/2.5/forecast?q="
                    + string[0] +"&units=imperial&APPID="+ apiKey);
            //string[0] the [0] is needed since we could send in mutiple strings
            //into the .execute (ex.) .execute(string,string2);

            return xml;
        }

        /**
         * After Executing task, now get the info we got from reading the file
         * and start adding it to the .xml and stuff like text view*/
        @Override
        protected void onPostExecute(String result) {

           try{
               //JSONObjects helps us parse through json files
               JSONObject jsonObject = new JSONObject(result);

               JSONArray current_main = jsonObject.getJSONArray("list");
               //To make code simpler in getting weather details
               JSONObject details = (JSONObject) current_main.getJSONObject(0).getJSONArray("weather").get(0);


                //If there's data in the obj then
               if(jsonObject != null){

                   city_label.setText(jsonObject.getJSONObject("city").getString("name").toUpperCase(Locale.US));
                   /**
                    * we are getting the array 'list' and then we are getting the first index which is the most recent
                    * weather forcast, from there we look into the array and get the object 'main' which has its
                    * own group of values and we grab the 'key' temp and grab its value
                    * we format all this into a decimal *///Adding + 3 since api seems to always be under actual weather
                   current_temp.setText(String.format("%.2f", current_main.getJSONObject(0).getJSONObject("main").getDouble("temp")+3)+ "°");

                   weathericon.setText(HtmlCompat.fromHtml(Function.setWeatherIcon(details.getInt("id")),HtmlCompat.FROM_HTML_MODE_LEGACY));
                   description.setText(details.getString("description"));

                   humidity_label.setText(getResources().getString(R.string.humidity) + " " +
                           current_main.getJSONObject(0).getJSONObject("main").getString("humidity") + "%");


                   //changing background depeding on time
                   /*long currentTime = new Date().getTime();
                   if(currentTime>=19) {
                       app_background.setBackgroundResource(R.drawable.night); //or whatever your image is
                       app_background.setAlpha(1);
                       setContentView(app_background); //you might be forgetting this
                   }else if(currentTime<19){
                       //do nothing for now
                   }*/
               }


               loader.setVisibility(View.GONE);
           }catch(JSONException e){
               Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
           }


        }
    }//DownloadWeather extends AsyncTask<String,Void,String>



}

