package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
// needed for displaying the recycle view vertically
import androidx.recyclerview.widget.LinearLayoutManager;
// rendering items
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
// these are the imports for the Views
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
// need for saving and changing the items todolist
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
// need for todolist saving as strings
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    // This is used to reference the passed data from Main to Edit Activity
    // from the messenger to the receiver
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    // code to refer to the intent
    public static final int EDIT_TEXT_CODE = 20;

    // make a list of items type string for the list
    List<String> items;

    //get components to handle them, need to import
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;

    // need field for ItemAdapter to refer to it in the OnLongClick
    ItemAdapter itemsAdapter;

    //    called by android system, the main activity will be created here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // create views and objects under

        // reference the Views (components)
        // can call methods directly
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        // this is the RecycleViewer
        rvItems = findViewById(R.id.rvItems);

//        // create empty list & add mock items
//        items = new ArrayList<>();
//        items.add("Buy Milk");
//        items.add("Gym");
//        items.add("Eat food");

        // instead of creating empty list we call load items (private method uses the member variable items)
        loadItems();


        // define an instance of the ItemAdapter.OnLongClickListener interface (no need to implement)
//        ItemAdapter.OnLongClickListener onLongClickListener = new ItemAdapter.OnLongClickListener() {
//            @Override
//            public void onItemLongClick(int position) {
//
//            }
//        };

        // can do lambda instead
        ItemAdapter.OnLongClickListener onLongClickListener = position -> {
//            remove the item from the list of items, delete from the model
            items.remove(position);
//            notify the ItemAdapter we removed the item, adapter notify
            itemsAdapter.notifyItemRemoved(position);
//            notify the user we removed the item (using a toast) & show it
            Toast.makeText(getApplicationContext(), "Item removed!", Toast.LENGTH_LONG).show();
            // call saveItems method when removing
            saveItems();
        };

        // define on click listener, when the layout is clicked
        ItemAdapter.OnClickListener onClickListener = position -> {
            // log to show if it is working correctly
            Log.d("MainActivity", "Single click as position: " + position);

            // create the new activity (edit activity - screen) - using an Intent (open URL, camera, activity)
            // MainActivity.this refers to the current instance (content we are calling), the EditActivity.class
            // is referring to the class which we want to go to. The Android system takes care of the creation
            // of the instance.
            Intent i = new Intent(MainActivity.this, EditActivity.class);
            // pass the data being edited along with the intent, & position(to know what was updated in MainActivity), & the item
            // we pass the actual content of the TodoItem, & position of the item to know what has been updated
            // we pass data using putExtra of the content
            // the key is always a string, we use to reference in the MainActivity & EditActivity

            // here we pass the Item
            i.putExtra(KEY_ITEM_TEXT, items.get(position));
            // here we pass the item position
            i.putExtra(KEY_ITEM_POSITION, position);
            // display the activity
            // the startActivityForResult - we expect a result (the updated item) back from EditActivity
            // this takes the Intent & request code (for multiple intents & activities) - to distinguish from
            // multiple Intents
            startActivityForResult(i, EDIT_TEXT_CODE);
        };

        // MainActivity.java constructs the item adapter
        // also pass the onLongClickListener
        itemsAdapter = new ItemAdapter(items, onLongClickListener, onClickListener);
        // set the item adapter to the RecycleViewer
        rvItems.setAdapter(itemsAdapter);
        // we also set a LayoutManager, this by default sets our things vertically
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // add button listener when it is clicked
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        //can use lambda instead
        btnAdd.setOnClickListener(v -> {
            // get the item from the editable text (returns Editable. convert to string)
            String item = etItem.getText().toString();
            // add the item to the List
            items.add(item);
            // alert the adapter an item as added, pass the position of the newly
            // inserted item, in our case - 1 the size of items
            itemsAdapter.notifyItemInserted(items.size() - 1);
            // clear the editable text after submission
            etItem.setText("");
            // call the save items when adding
            saveItems();

            // notify user of submission by creating a toast & show it
            Toast.makeText(getApplicationContext(), "Added todo item!", Toast.LENGTH_SHORT).show();
        });
    }

    // handle the data being passed from the EditActivity when calling the onClickListener startActivityFromResult
    // this method must be outside the onCreate method since it handles and overrides the results after
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // redundant - since we have only 1 activity
        // we have to check is the requestCode matches the EDIT_TEXT_CODE which we passed
        // for the startActivityForResult in Main's onClickListener & the resultCode matches the RESULT_OK code returned from
        // the EditActivity
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // retrieve the updated text value, check null
            String itemText = Objects.requireNonNull(data).getStringExtra(KEY_ITEM_TEXT);
            // extract the original position of the edited item from the position key
            int itemPosition = data.getExtras().getInt(KEY_ITEM_POSITION);
            // update the model with the new item using the position
            // we use the setMethod to update for Lists
            items.set(itemPosition, itemText);
            // notify the adapter of the new change at the position
            itemsAdapter.notifyItemChanged(itemPosition);
            // call the save method for the new changes, persist the data
            saveItems();
            // show a toast that the item was updated successfully
            Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
        } else {
            // log warning if they do not match
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }

    }

    // will return the file in which we will store our list of todoItems
    private File getDataFile() {
        // directory of the app, and the local file
        return new File(getFilesDir(), "data.txt");
    }

    // this function will load items by reading every line of data.txt file
    // will be called when the app starts up (initial call)
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            // logging the exception (tag [by convention the class name], the message, and exception)
            Log.e("MainActivity", "Error reading items", e);
            // if we do encounter an exception, set an empty ArrayList (so it wont crash and build of an empty list)
            items = new ArrayList<>();
        }
    }

    // this function saves items by writing them into the local data file
    // called whenever there is a change in the todoitems (removing & adding)
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}