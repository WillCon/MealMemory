package com.orain.mealmemory;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

public class ReviewActivity extends AppCompatActivity {

    private ListView listView;
    private ReviewAdapter myArrayAdapter;
    private boolean deleting;
    private String currentRest;
    private ArrayList<Review> reviews;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //State of deleting manages handling of clicking a review
        //If deleting is true it will confirm and delete the review, if false it does nothing
        deleting = false;

        String[] filesList = fileList();
        for (String fileName : filesList) {
            Log.d("Files (Review)", fileName);
        }

        //Get name of currently edited restaurant from the MainActivity
        Intent intent = getIntent();
        currentRest = intent.getStringExtra("Restaurant");
        setTitle(currentRest);

        //Populate reviews list
        reviews = new ArrayList<>(); //List of all individual meal reviews at that restaurant
        try {
            BufferedReader in = new BufferedReader(new FileReader(getFilesDir() + "/" + currentRest));
            String nextReview;
            while((nextReview = in.readLine()) != null){
                if(nextReview.equals("")){
                    continue;
                }
                StringTokenizer tokens = new StringTokenizer(nextReview, "|");
                String nextName = tokens.nextToken();
                String nextRating = tokens.nextToken();
                addReview(nextName, Integer.parseInt(nextRating));
            }
            in.close();
        } catch (IOException e) {
            Toast errorMessage = Toast.makeText(getApplicationContext(), "Cannot Read File", Toast.LENGTH_SHORT);
            Log.e("Read Failed", e.getMessage());
            errorMessage.show();
        }

        myArrayAdapter = new ReviewAdapter(this, R.layout.reivew_item, reviews);

        //Setting up delete click handler
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(myArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteRow(position);
            }
        });

        final Button cancelDelete = (Button) findViewById(R.id.doneDeleting);
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleting = false;
                LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
                deletingBar.setVisibility(View.GONE);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //If in the deleting state confirm with user and remove that review
    private void deleteRow(int position) {
        if (deleting) {
            final Review theReview = (Review) listView.getItemAtPosition(position);
            final String theReviewString = theReview.getMealName();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Meal");
            builder.setMessage("Delete " + theReviewString + "?");

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeReview(theReviewString);
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
    protected void onResume() {
        super.onResume();
        deleting = false;
        LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
        deletingBar.setVisibility(View.GONE);

        sortReviews();
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
        if (id == R.id.addItem) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            final boolean color = sharedPref.getBoolean("color_switch", true);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Meal");
            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            // Set up the input
            final EditText input = new EditText(this);
            input.setHint("Meal");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            input.setLayoutParams(layoutParams);
            layout.addView(input);

            final Spinner dropdown = new Spinner(this);
            if (color) {
                Integer[] items = new Integer[]{R.mipmap.ic_green, R.mipmap.ic_orange, R.mipmap.ic_red};
                ImageSpinner adapter = new ImageSpinner(this, items);
                dropdown.setAdapter(adapter);
                layout.addView(dropdown);
            } else {
                String[] items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
                dropdown.setAdapter(adapter);
                layout.addView(dropdown);
            }

            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int temp = dropdown.getSelectedItemPosition();
                    if (color) {
                        if (temp == 0) {
                            temp = 10;
                        } else if (temp == 1) {
                            temp = 5;
                        } else if (temp == 2) {
                            temp = 1;
                        }
                    } else {
                        temp++;
                    }
                    if(input.getText().toString().contains("|")){
                        Toast errorMessage = Toast.makeText(getApplicationContext(), "Meal cannot contain '|' character.", Toast.LENGTH_SHORT);
                        errorMessage.show();
                    }
                    addReview(input.getText().toString(), temp);
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
        if (id == R.id.removeItem) {

            deleting = true;
            LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
            deletingBar.setVisibility(View.VISIBLE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause(){
        super.onPause();

        try{
            FileWriter FO = new FileWriter(getFilesDir() + "/" + currentRest);
            for (Review r : reviews){
                FO.write(r.getMealName() + '|' + r.getRating() + "\n");
            }
            FO.close();
        }catch (IOException e){
            Toast errorMessage = Toast.makeText(getApplicationContext(), "Cannot Write File" , Toast.LENGTH_SHORT);
            Log.e("Write Failed", e.getMessage());
            errorMessage.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    //Adds a new review of name n and rating r, returns false if that meal already exists
    private boolean addReview(String n, int r) {
        if (findReview(n) != -1) {
            Toast errorMessage = Toast.makeText(getApplicationContext(), "Meal Already Exists" , Toast.LENGTH_SHORT);
            errorMessage.show();
            return false;
        }
        int loc = reviews.size();
        Review temp = new Review(n, r);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String sortType = sharedPref.getString("review_sort", "0");
        switch (sortType){
            case "0":
                for(int i = 0; i < reviews.size(); i++){
                    //Log.d("Review Insert", reviews.get(i).getMealName() + " compared to " + temp.getMealName() + " = " + reviews.get(i).compareTo0(temp));
                    if(reviews.get(i).compareTo0(temp) > 0){
                        loc = i;
                        break;
                    }
                }
                break;
            case "1":
                for(int i = 0; i < reviews.size(); i++){
                    if(reviews.get(i).compareTo1(temp) > 0){
                        loc = i;
                        break;
                    }
                }
                break;
            case "2":
                for(int i = 0; i < reviews.size(); i++){
                    if(reviews.get(i).compareTo2(temp) > 0){
                        loc = i;
                        break;
                    }
                }
                break;
            case "3":
                for(int i = 0; i < reviews.size(); i++){
                    if(reviews.get(i).compareTo3(temp) > 0){
                        loc = i;
                        break;
                    }
                }
                break;
            default:
                break;
        }
        reviews.add(loc, temp);
        return true;
    }

    //Removes review of name n, returns true if it removes something, otherwise returns false
    private boolean removeReview(String n){
        final int temp = findReview(n);
        if(temp != -1){
            reviews.remove(temp);
            return true;
        }
        return false;
    }

    //Returns index of review matching name n, no matching review returns -1
    private int findReview(String n){
        for(int i = 0; i < reviews.size(); i++){
            if(reviews.get(i).getMealName().equals(n)){
                return i;
            }
        }
        return -1;
    }

    private void sortReviews(){
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String sortType = sharedPref.getString("review_sort", "0");
        switch (sortType){
            case "0":
                Collections.sort(reviews, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.compareTo0(rhs);
                    }
                });
                break;
            case "1":
                Collections.sort(reviews, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.compareTo1(rhs);
                    }
                });
                break;
            case "2":
                Collections.sort(reviews, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.compareTo2(rhs);
                    }
                });
                break;
            case "3":
                Collections.sort(reviews, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.compareTo3(rhs);
                    }
                });
                break;
            default:
                Collections.sort(reviews, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.compareTo0(rhs);
                    }
                });
        }
    }

}
