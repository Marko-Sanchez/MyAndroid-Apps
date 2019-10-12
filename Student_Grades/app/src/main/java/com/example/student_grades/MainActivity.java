package com.example.student_grades;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        TypefaceProvider.registerDefaultIconSets();
    }

    /**
     * Called when log In button is pressed. Get text in both
     * 'User name' and 'PassWord' text boxes. Starts Activity that
     * shows users grade alongside a bar graph.
     * @param view of type BootstrapEditText
     * */
    public void onClickLogIn(View view) {
        BootstrapEditText password = findViewById(R.id.edit_text_password);
        BootstrapEditText username = findViewById(R.id.edit_text_user_name);

        final String PASSWORD = password.getText().toString();// user Input string password.
        final String USER_NAME = username.getText().toString();// user input string user name.

        // get Fire base object
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReferenceFromUrl("https://student-info-37bea.firebaseio.com/students/"+USER_NAME);

        // Listener to be able to access a snapshot of the database since it's updated in real time.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // USER_NAME is empty or null
                if (USER_NAME == null || USER_NAME.equals("")) {

                    FancyToast.makeText(getBaseContext(),"User Name or Password incorrect",Toast.LENGTH_LONG,FancyToast.ERROR,true).show();

                }else if(dataSnapshot.exists()){

                    // Since the User exist, now check if the passwords match
                    DataSnapshot dataSnapshot1 = dataSnapshot.child("Password");
                    String temp_password = String.valueOf(dataSnapshot1.getValue());

                    if(temp_password.equals(PASSWORD)){

                        Intent intent = new Intent(MainActivity.this, student_info.class);
                        intent.putExtra("username", USER_NAME);
                        startUser_info(intent);

                    }else{

                        FancyToast.makeText(getBaseContext(),"User Name or Password incorrect",Toast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }


                }else{
                    Log.d("mk", "onDataChange:User_name; " + USER_NAME+ " Does not Exist");
                    FancyToast.makeText(getBaseContext(),"User Name or Password incorrect",Toast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Starts Student Info Activity; where they see there grades and stuff
    public void startUser_info(Intent intent){
        startActivity(intent);
        Animatoo.animateSplit(this); //fire the slide left animation

    }
    // Starts Activity; Where user can create there profile
    public void createNewUser(View view) {
        Intent intent = new Intent(this,CreateNewUser.class);
        startActivity(intent);
        Animatoo.animateZoom(this);
    }

}
