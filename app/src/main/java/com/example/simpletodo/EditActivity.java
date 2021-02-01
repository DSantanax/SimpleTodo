package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

// used to pass data
import android.content.Intent;
import android.os.Bundle;
// used for the Views
import android.widget.Button;
import android.widget.EditText;
// to check for non null
import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    EditText upItem;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // first show the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // create reference to the views
        upItem = findViewById(R.id.upItem);
        btnSave = findViewById(R.id.btnSave);

        // set the title of the app section, check if it is null
        Objects.requireNonNull(getSupportActionBar(), "Action Bar must not be null").setTitle("Edit item");
        // Using the intent get the Item we passed using the KEY we set in the main activity (public)
        // and set this to our upItem, this will set the current item we have
        upItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));
        // user clicks button when they want to save the edited item
        btnSave.setOnClickListener(l -> {
            // when the btn is selected, we want to return back to the main activity
            // create an intent which will contain the results, use as en empty constructor
            // mainly used as a shell to pass data
            Intent intent = new Intent();
            // pass the data (results of editing), as String with the same KEY
            intent.putExtra(MainActivity.KEY_ITEM_TEXT, upItem.getText().toString());
            // pass the position using Main's KEY, use this to update the position in the list from Main
            // take in the key, & the position to update (passed from Main, using it again)
            intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
            // set the results of the intent (define the results), since in Main we used startActivityForResult
            // using RESULT_OK predefined in Android & the intent data we used
            setResult(RESULT_OK, intent);
            // finish activity, close the current screen and go back to Main
            finish();
        });
        // In this class we return the updated edited item, the position, and close the activity
        // the Main activity will use this to update the TodoItem list
    }
}