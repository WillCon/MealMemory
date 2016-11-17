package com.orain.mealmemory;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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

public class ReviewActivity extends AppCompatActivity {

    private String rest;
    private ListView listView;
    private ReviewAdapter myArrayAdapter;
    private boolean deleting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //State of deleting manages handling of clicking a review
        //If deleting is true it will confirm and delete the review, if false it does nothing
        deleting = false;

        Intent intent = getIntent();
        rest = intent.getStringExtra("Restaurant");

        Globals g = Globals.getInstance();

        myArrayAdapter = new ReviewAdapter(this, R.layout.reivew_item, g.getReviews(rest));

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
        cancelDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                deleting = false;
                LinearLayout deletingBar = (LinearLayout) findViewById(R.id.deleteBar);
                deletingBar.setVisibility(View.GONE);
            }
        });
    }

    //If in the deleting state confirm with user and remove that review
    private void deleteRow(int position){
        if(deleting){
            final Review theReview = (Review) listView.getItemAtPosition(position);
            final String theReviewString = theReview.getMealName();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Meal");
            builder.setMessage("Delete " + theReviewString + "?");

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Globals g = Globals.getInstance();
                    g.removeReview(rest, theReviewString);
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
            if(color) {
                Integer[] items = new Integer[]{R.mipmap.ic_green, R.mipmap.ic_orange, R.mipmap.ic_red};
                ImageSpinner adapter = new ImageSpinner(this, items);
                dropdown.setAdapter(adapter);
                layout.addView(dropdown);
            }
            else{
                String[] items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
                dropdown.setAdapter(adapter);
                layout.addView(dropdown);
            }

            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Globals g = Globals.getInstance();
                    int temp = dropdown.getSelectedItemPosition();
                    if(color) {
                        if (temp == 0) {
                            temp = 10;
                        } else if (temp == 1) {
                            temp = 5;
                        } else if (temp == 2) {
                            temp = 1;
                        }
                    }
                    else{
                        temp++;
                    }
                    g.addReview(rest, input.getText().toString(), temp);
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

}
