package com.orain.mealmemory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {


    private ArrayAdapter myArrayAdapter;
    private boolean deleting;
    private ArrayList<String> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deleting = false;


        //Get list of restaurant files and store in list
        restaurants = new ArrayList<String>(); //List of all restaurants
        String[] filesList = fileList();

        restaurants.addAll(Arrays.asList(filesList));
        Collections.sort(restaurants, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });

        myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, restaurants);

        ListView listView;
        listView = (ListView) findViewById(R.id.mainList);
        listView.setAdapter(myArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(deleting){
                    deleteRow(position);
                }
                else {
                    openReviews(restaurants.get(position));
                }
            }
        });
        //listView.setTextFilterEnabled(true);

        final Button cancelDelete = (Button) findViewById(R.id.doneDeleting);
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleting = false;
                LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
                deletingBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteRow(final int position){
        if(deleting){
            //final Restaurant theRest = (Restaurant) listView.getItemAtPosition(position);
            //final String theRestString = theRest.getName();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Meal");
            //builder.setMessage("Delete " + theRestString + "?");
            builder.setMessage("Delete " + restaurants.get(position) + "?");

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteFile(restaurants.get(position));
                    restaurants.remove(position);
                    myArrayAdapter.notifyDataSetChanged();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        deleting = false;
        LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
        deletingBar.setVisibility(View.GONE);

        myArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        if(id == R.id.addItem){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Restaurant");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addRestaurant(input.getText().toString());
                    myArrayAdapter.notifyDataSetChanged();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            return true;
        }
        if(id == R.id.removeItem){
            deleting = true;
            LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
            deletingBar.setVisibility(View.VISIBLE);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void openReviews(String r){
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("Restaurant", r);
        startActivity(intent);
    }

    private boolean addRestaurant(String newRest){
        int comparison = 0;
        int i = 0;
        for (; i < restaurants.size(); i++){
            comparison = restaurants.get(i).compareToIgnoreCase(newRest);
            if(comparison == 0) {
                Toast errorMessage = Toast.makeText(getApplicationContext(), "Restaurant Already Exists" , Toast.LENGTH_SHORT);
                errorMessage.show();
                return false;
            }
            else if(comparison > 0){
                break;
            }
        }
        restaurants.add(i, newRest);
        try {
            FileOutputStream fos = openFileOutput(newRest, Context.MODE_PRIVATE);
            fos.close();
        }
        catch (IOException e){
            Toast errorMessage = Toast.makeText(getApplicationContext(), "Cannot Create File", Toast.LENGTH_SHORT);
            errorMessage.show();
            return false;
        }
        return true;
    }
}
