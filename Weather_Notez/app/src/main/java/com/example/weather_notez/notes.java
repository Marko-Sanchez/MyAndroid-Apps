package com.example.weather_notez;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *  A weather app I created before fused with a note app I created into one.
 * @author Marco Santi
 */
public class notes extends AppCompatActivity {

    private String[] list;                              // list originally containing nothing
    private ListView listView;
    private EditText editText;                          // The actual 'notepad'
    private int item_position = 0;                      // Holds current list selected position
    private ArrayList<String> arrayList;                //Holds Strings to add to ArrayAdapter
    private ArrayAdapter<String> stringArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Shares preferences for array list gets info from previous times
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String temp = prefs.getString("arrayList",null);

        // Checks to see if there are list items from previous runs;
        if (temp != null) {
            // Since the list items where added together the ','
            // helped us differentiate between note names
            list = temp.split(",");
        }

        // ArrayList so we can add more items.
        arrayList = new ArrayList<String>();
        if(list != null)
        arrayList.addAll(Arrays.asList(list));

        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.simplerow
                , arrayList);

        listView = findViewById(R.id.notes_list);
        listView.setAdapter(stringArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //When another item is selected the previous item/note is saved
                saveText();

                //Want it to load that note for that specific item selected
                item_position = position;
                showText(item_position);
            }

        });

        // Method to display text using file in android
        showText(item_position);
    }

    /**
     * Gets called in the addNotes and deleteNotes method: To save the removed/added
     * list items onto a SharedPreference inorder to summoned again if app is re-run.
     * @see #addNotes(View)
     * @see #deleteNote(View)
     */
    public void updateSavedData(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        // Use the StringBuilder to construct a new String by concatenating all list items
        // and separating with ','
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arrayList.size(); i++) {
            sb.append(arrayList.get(i)).append(",");
        }

        // Replaces the old saved list with this new list
        prefsEditor.putString("arrayList", sb.toString());

        prefsEditor.apply();
    }// updateSavedData()


    /**
     *  Gets called when '+' button gets clicked in activity; creates a new note
     *  by getting the string that was typed in the edit text next to it.
     * @param view Button widget.
     * @see #listView (String)
     */
    public void addNotes(View view) {
        EditText new_notes_text = findViewById(R.id.notes_create_new_note);

        // If no Note name is typed ignore the button event
        if (new_notes_text.getText() != null) {
            arrayList.add(new_notes_text.getText().toString());

            // Informs adapter a change was made inorder to update widget items
            stringArrayAdapter.notifyDataSetChanged();

            new_notes_text.setText("");

           updateSavedData();
        }
    }// addNotes()

    /**
     * Gets called when 'X' button is clicked. Removes the current item selected
     * from the list-view and moves the saved files in android down a step ex. deleting '2' moves
     * '3' into '2' and '4' into '3.' Then calls updateSavedData
     * @param view Button widget.
     * @see #updateSavedData()
     * @throws FileNotFoundException is thrown when attempting to call a file.
     */
    public void deleteNote(View view) {
        Toast.makeText(this,"Deleting: "+listView.getItemAtPosition(item_position),
                Toast.LENGTH_LONG).show();

        int array_size = arrayList.size();

        // Removes current selection from ArrayList, then calls notifyDataSetChanged to update ArrayAdapter
        arrayList.remove(item_position);
        stringArrayAdapter.notifyDataSetChanged();

        // For-loop gets all the 'note' txt files above the current 'item_position' and moves them down
        for(int i = (item_position + 1) ;i <= array_size;i++){
                try{
                    //Get text from file (i) and save it to temporary variable
                    Scanner scanner = new Scanner(this.openFileInput("notes"+ i +".txt"));
                    String alltext = "";

                    while(scanner.hasNextLine()){
                        String line = scanner.nextLine();

                        alltext += line;
                    }//while(scanner.hasNextLine())

                    scanner.close();

                    // Get the temporary variable and save its' text to (i - 1) note
                    PrintStream output = new PrintStream(this.openFileOutput("notes"+
                            ( i - 1 ) + ".txt", MODE_PRIVATE));

                    // Write to file, then close PrintStream
                    output.println(alltext);
                    output.close();

                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }

        }// for(int i = (item_position + 1) ;i <= array_size;i++)



        // Since we deleted a list item we have to update the info in Shared preferences.
        updateSavedData();
        showText(item_position);
    }// deleteNote(View view)

    /**
     *  Gets text from a file, from within android, and displays it onto edit text view.
     *  Gets called from 'onCreate', 'deleteNote,' and List-View 'onClickListener.'
     * @param position integer referencing item selected in list view.
     * @see #onCreate(Bundle)
     * @throws FileNotFoundException is thrown when attempting to call a file.
     */
    public void showText(int position){
        editText = findViewById(R.id.notes_text);
        editText.setText("");

        try{
            //Open File and read text inside it.
            Scanner scanner = new Scanner(this.openFileInput("notes"+ position +".txt"));
            String alltext = "";

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                alltext += line;
            }

            //Display text on edit text view
            editText.setText(alltext);
            scanner.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets called from within onPause; saves text.
     * @throws FileNotFoundException is thrown when attempting to call a file.
     * @see #onPause()
     * */
    public void saveText(){
        editText = findViewById(R.id.notes_text);

        // Get user text from edit text view
        String text_to_save = editText.getText().toString();

        // Create a file in android storage and save the data to it
        try{
            // Saves the text of the last item selected before leaving
            PrintStream output = new PrintStream(this.openFileOutput("notes"
                    + item_position + ".txt", MODE_PRIVATE));

            output.println(text_to_save);
            output.close();

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        Toast.makeText(notes.this,(R.string.text_saved),Toast.LENGTH_SHORT).show();
    }


    /**
     * When the user exits the Note Activity either by leaving the
     * app or going back to the previous Activity the users text will automatically
     * be saved when the onPause method is called by android itself.
     * @see #saveText()
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveText();
    }

}