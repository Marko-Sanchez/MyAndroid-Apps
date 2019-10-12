package com.example.student_grades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapWell;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateNewUser extends AppCompatActivity {
    private ArrayList<String> stringArrayList;      // Holds a String to be displayed in a ListView
    private Map<String,String> courses_grades;      // Contains key:course, value:grade to be added to database
    private String FIREBASE_URL = "https://student-info-37bea.firebaseio.com/"; // URL to Database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_new_user);
        stringArrayList = new ArrayList<>();
        courses_grades = new HashMap<>();
    }


    /**
     * Called when user clicks on "ADD COURSE" button. Gets course and grade, of type BootstrapEditText,
     * adds them to a Map<course,grade> of type String for later use.Then also adds
     * them to a ArrayList to apply to a ArrayAdapter to be displayed in a ListView.
     * @param view Button widget
     * @see ArrayList {@link #stringArrayList} A sentence is created,using strings from EditText, and added to this ArrayList
     * @see Map {@link #courses_grades} Adds the values of s_course as key and s_grade as its' value
     * */
    public void addNewUserCourses(View view) {
        BootstrapEditText course = findViewById(R.id.CreateNewUser_Courses);
        BootstrapEditText grade = findViewById(R.id.CreateNewUser_Grades);

        String s_course = course.getText().toString();
        String s_grade = grade.getText().toString();

        courses_grades.put(s_course,s_grade);

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        stringArrayList.add("Course: "+s_course+" Grade: "+s_grade);
        stringArrayAdapter.addAll(stringArrayList);

        // Get container
        BootstrapWell bootstrapWell = findViewById(R.id.listview_Well);

        // Call ListView in container
        ListView listView  = (ListView) bootstrapWell.getChildAt(0);

        listView.setAdapter(stringArrayAdapter);

    }

    /**
     * @param view Button Widget
     * @see Map {@link #courses_grades} Iterates though the keys and gets there values
     * */
    public void CreateNewUser_Create(View view) {
        // Get String Array
        // Get User name
        // Get PassWord
        // Create In fire base
        BootstrapEditText user_name = findViewById(R.id.CreateNewUser_user_name);
        BootstrapEditText password = findViewById(R.id.CreateNewUser_password);

        String s_user_name = user_name.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReferenceFromUrl(FIREBASE_URL);

         DatabaseReference create_user = myRef.child("/students");

         create_user.child(s_user_name).child("Password").setValue(password.getText().toString());// Creates User, and sets the password
         create_user.child(s_user_name).child("Grades");// Create Grades

        DatabaseReference user = create_user.child(s_user_name).child("Grades");

        for(String key: courses_grades.keySet()){
            user.child(key).setValue(courses_grades.get(key));// Sets Course and Grade
        }

        FancyToast.makeText(this,"USER "+s_user_name+" CREATED", Toast.LENGTH_LONG,FancyToast.SUCCESS,true);

        startActivity(new Intent(this,MainActivity.class));
        Animatoo.animateCard(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideLeft(this); // When he back button is pressed, fire the slide left animation
    }

}
