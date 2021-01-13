package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;
// needed for displaying the recycle view vertically
import androidx.recyclerview.widget.LinearLayoutManager;
// rendering items
import androidx.recyclerview.widget.RecyclerView;

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


public class MainActivity extends AppCompatActivity {


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
        // MainActivity.java constructs the item adapter
        // also pass the onLongClickListener
        itemsAdapter = new ItemAdapter(items, onLongClickListener);
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
    // will return the file in which we will store our list of todoItems
    private File getDataFile(){
        // directory of the app, and the local file
        return new File(getFilesDir(), "data.txt");
    }
    // this function will load items by reading every line of data.txt file
    // will be called when the app starts up (initial call)
    private void loadItems(){
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