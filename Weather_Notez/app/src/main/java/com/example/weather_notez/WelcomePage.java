package com.example.weather_notez;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import java.time.Duration;
import java.util.Locale;

/**06/21/2019 took roughly 4 days,hardest part was reading the json and working with the weather
 * api*/
public class WelcomePage extends AppCompatActivity {
/**Since we are using two activity's remembers to declare them both in
 * android manifest
 * Design From:https://www.youtube.com/watch?v=-8QrtXkfF9A&t=1s
 * Update no Longer using the above; Now change to a moving palm tree background
 * with help from:
 * :https://o7planning.org/en/10487/android-mediaplayer-and-videoview-tutorial*/

ConstraintLayout myLayout;
AnimationDrawable animationDrawable;

VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        getSupportActionBar().hide();

        // Uncomment if you want changing gradient effect and comment see_video()
        /*
        myLayout = findViewById(R.id.myLayout);
        animationDrawable =(AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();
        */
        videoView = findViewById(R.id.video_view);

        //Comment if you want different background
        setVideoView();

    }

    public void setVideoView(){
        try {
            // ID of video file.
            int id = this.getRawResIdByName("palm_tree");
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + id));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                mp.setLooping(true);
            }
        });
    }
    // Find ID corresponding to the name of the resource (in the directory raw).
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
        Log.i("AndroidVideoView", "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }

    //did the whole click listener through xml using android:onClick
    public void onClick(View view){
        Toast.makeText(WelcomePage.this,R.string.Processing,Toast.LENGTH_LONG).show();

        // Jumps to Weather forecast Activity
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    /**
     *  Changes Languages to either English,Spanish, or Japanese; Uses Dialogs to prompt user
     *  @param view Button widget
     *  */
    public void changeLanguage(View view) {

        // Create Dialog
        final Dialog dialog =  new Dialog(this);

        dialog.setTitle(R.string.language);// Doesn't work
        dialog.setContentView(R.layout.fragment_language_fragment);

        // Gets RadioGroup ID from dialog which contains the layout that hold the RadioGroup
        RadioGroup rg = dialog.findViewById(R.id.radiogroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.en: {// English
                        String languageToLoad  = "en"; // your language
                        Locale locale = new Locale(languageToLoad);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.setLocale(locale); //= locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                    }
                        break;
                    case R.id.es: { // Spanish
                        String languageToLoad  = "es"; // your language
                        Locale locale = new Locale(languageToLoad);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.setLocale(locale); //= locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());

                    }
                        break;
                    case R.id.ja: {// Japanese
                        String languageToLoad  = "ja"; // your language
                        Locale locale = new Locale(languageToLoad);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.setLocale(locale); //= locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                    }
                        break;
                        default: {// In case check id gives weired int, set to English
                            String languageToLoad  = "en"; // your language
                            Locale locale = new Locale(languageToLoad);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.setLocale(locale); //= locale;
                            getBaseContext().getResources().updateConfiguration(config,
                                    getBaseContext().getResources().getDisplayMetrics());
                        }
                        break;
                }
                // Refreshes Activity
                Intent intent = new Intent(getBaseContext(),WelcomePage.class);
                finish();
                startActivity(intent);
            }
        });

        //Show the dialog
        dialog.show();
    }
}
