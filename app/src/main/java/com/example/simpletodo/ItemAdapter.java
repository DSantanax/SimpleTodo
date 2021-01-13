package com.example.simpletodo;

/*
    The item adapter (adapter, manager, and [animator]) is needed for the recycle view

 */

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Each RecycleView needs an adapter, the adapter needs a ViewHolder as well

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>  {

    // member variable - used to fill the adapter with items
    List<String> items;
    //  member variable - refer to the OnLongClickListener
    OnLongClickListener longClickListener;

    // we nee to pass info from main activity to item adapter
    // can do this by defining an interface in ItemsAdapter that the MainActivity will implement
    // all interface methods are abstract
    public interface OnLongClickListener {
        // this method will only have 1 param
        // the class implementing this method (MainActivity) needs to know the position
        // of where we did the long press, to notify the adapter that is the position of where
        // we delete the item
        void onItemLongClick(int position);
    }

    // main piece of info we need is data about the model
    // in our case the list of strings
    // we also need to take the longClickListener to notify the ViewHolder
    public ItemAdapter(List<String> items, OnLongClickListener longClickListener) {
        this.items = items;
        // set this to the passed param from MainActivity to use it
        this.longClickListener = longClickListener;
    }

/*
    need to override all three methods for the Adapter
 */

//    onCreateViewHolder to inflate the item layout and create the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new View & wrap it inside the ViewHolder
        // using LayoutInflator to inflate a view
        // wrap it inside a View Holder & return it

        // here we use a LayoutInflator to inflate a view
        // using the built in layout simple_list_item_1 for the view (from android resource files),
        // we also need the root (in our case the parent), and false since the RecycleView bill be attaching this view
        // rather than attaching to the root/parent
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        // we return the newly created View wrapped inside a ViewHolder
        return new ViewHolder(todoView);
    }

//    to set the view attributes based on the data to put into a ViewHolder
    // responsible to bind data to a particular view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // grab the item at the position
        String item = items.get(position);
        // bind the item into the specified ViewHolder
        holder.bind(item);
    }

//    to determine the number of items available in the data
    // this tells the RV how many items are in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Container that provides access to views that represent each row of the list
    // we use this for the Views
    // non-static inner class
    class ViewHolder extends RecyclerView.ViewHolder {

        // simple_list_item_1 has an id of text1, to get a reference to the TextView
        TextView tvItem;

        // We also create a constructor that accepts the entire item row
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            // we use the View from the android R built in to find the text1 and assign it to the tvItem TextView
            tvItem = itemView.findViewById(android.R.id.text1);
        }

        // update the View inside of the View Holder with this data
        public void bind(String item) {
            // set the text for the text view
            tvItem.setText(item);

//            set an OnLongClick listener for the item
            tvItem.setOnLongClickListener(l -> {
                // we notify the listener that which position as the one long pressed

                // non-static inner classes can refer to outside variables (non-static)
                // we use getAdapterPosition (from the RecycleView) to refer to where the ViewHolder is for the item
                //remove the item from the recycler view from the MainActivity, which implements the
                // OnClickListener
                longClickListener.onItemLongClick(getAdapterPosition());
                // return true to specify it was long clicked
                return true;
            });
        }

    }
}
