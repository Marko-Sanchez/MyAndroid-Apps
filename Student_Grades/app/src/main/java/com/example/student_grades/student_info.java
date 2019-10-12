package com.example.student_grades;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Collections;

public class student_info extends AppCompatActivity {
    private String FIREBASE_URL = "https://student-info-37bea.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        // Intent holds the users name
        Intent intent = getIntent();

        final String user_name = intent.getStringExtra("username");


        setTitle(user_name.toUpperCase() +"'S\t Grades");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReferenceFromUrl(FIREBASE_URL);

        final Query query = myRef.child("/students");



        //final Query query = myRef.child("/students/Marko/Grades/CS108");
        // Read from the database
        query.addValueEventListener(new ValueEventListener() {
            /*
             *Students:{
             * Jacob, Marko
             * }
             * */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // checks if the user name exist
                if(!dataSnapshot.child(user_name).exists()) {
                    // Toast might not be necessary since previous activity checks for user
                    FancyToast.makeText(getBaseContext(), "User Does Not Exist", Toast.LENGTH_LONG,FancyToast.ERROR,true).show();

                }else {
                    String stuff = dataSnapshot.child(user_name).child("Grades").getValue().toString();
                    Log.d("mk", stuff);

                    // Marko{
                    //  Grades{
                    //  { CS107=B,
                    //  CS108=A
                    //  }
                    //}}
                    ArrayList<String> strings = new ArrayList<String>();
                    for(DataSnapshot dataSnapshot1: dataSnapshot.child(user_name).child("Grades").getChildren()){
                        strings.add(dataSnapshot1.getKey());// Gets class
                        strings.add(dataSnapshot1.getValue().toString());// gets grade
                    }

                    for( int i = 0; i< strings.size();i++){
                        Log.d("lk", "onDataChange: "+strings.get(i));
                    }


                    display_Course_Grades(strings);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MK", "Failed to read value.", error.toException());
            }
        });
    }

    //  {CS107=B, CS108=A}
    /**
     * Since this method is in a listener it will update the listView and Graph when a
     * new value is entered in the database
     * @param strings ArrayList holds the course in position,(i), grade in position (i + 1)
     * @see #onCreate(Bundle) addValueEventListener
     * @see #showGraph(ArrayList)
     * */
    public void display_Course_Grades(ArrayList strings){
        ListView listView = findViewById(R.id.student_info_list_view);
        // In order to hold and place into list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1);


        // iterate through vector to get course and grade
        for (int i = 0; i < strings.size(); i++) {
                String course = strings.get(i).toString();
                String grade = strings.get( i+1).toString();
                String temp = "Course: " + course +"\t\t Grade: "+grade;
                adapter.add(temp);
                ++i;// since we added/ used two elements
        }


        listView.setAdapter(adapter);
        showGraph(strings);

    }

    /**
     * Shows Bar Graph from jjoe64 library and displays it. Counts how many times a specific
     * Letter grade occurs in the param.Gets called from display_Course_Grades, which passed
     * a ArrayList that holds all the Courses and Grades for each side by side.
     * @param arrayList of type String holds {course , grade,...,course,grade}
     * @see #display_Course_Grades(ArrayList) parent method
     * */
    public void showGraph(ArrayList arrayList){

        GraphView graph = findViewById(R.id.student_info_bar_graph);
        graph.removeAllSeries();// Clears previous input to create new one

        // Collections.frequency(arrayList,"A"); lets us count how many times this letter appears in the array
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, Collections.frequency(arrayList,"D")),// D
                new DataPoint(1, Collections.frequency(arrayList,"C")),// C
                new DataPoint(2, Collections.frequency(arrayList,"B")),// B
                new DataPoint(3, Collections.frequency(arrayList,"A"))// A
        });

        graph.addSeries(series);


        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"D", "C", "B","A"," "});// the " " is to make them all be able to show,jerry rigged
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(4);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(6);

        // styling, sets bar colors randomly
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });

    }

    /**
     * Gets called from button event in StudentInfo.xml Opens a AlertDialog that contains two editText
     * widgets; which will ask for Course:name and Grade:letter.
     * @param view button of type BootStrapButton opens AlertDialog.
     * @see #addCourse_Grade(String, String) Calls method once "ADD" button is pressed.
     * */
    public void addCourse_Grade(View view) {

        // Create layout inflater, so we can communicate with widgets then place into Alert Dialog.
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View  alert_view = layoutInflater.inflate(R.layout.alertdialog_view,null,false);

        // Edit text boxes in Alert Dialog
        final BootstrapEditText course_entry_text = alert_view.findViewById(R.id.course_EditText);
        final BootstrapEditText grade_entry_text = alert_view.findViewById(R.id.grade_EditText);

        // Build Alert Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ADD CLASS & GRADE")
                .setView(alert_view)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call another method or some shit
                        Log.d("mk", "addCourse_Grade: "+course_entry_text.getText().toString());
                        addCourse_Grade(course_entry_text.getText().toString(),grade_entry_text.getText().toString());

                    }
                })
                .setNeutralButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close dialog
                        dialog.dismiss();
                    }
                });


        // Create Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    /**
     * */
    public void addCourse_Grade(String course,String grade){

           // Get Intent that holds who the user is;User_name
            Intent intent = getIntent();
            final String user_name = intent.getStringExtra("username");

            // Get the Database
            FirebaseDatabase myref = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = myref.getReferenceFromUrl(FIREBASE_URL+"students/" + user_name);

            DatabaseReference gradesRef = databaseReference.child("Grades");

            // If the child does'nt exist it creates it; if it does it overrides it.
            gradesRef.child(course).setValue(grade);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this); // When he back button is pressed, fire the slide left animation
    }



}
