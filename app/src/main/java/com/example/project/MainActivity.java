package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.Comparator;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JsonTask.JsonTaskListener{

    public final String JSON_URL = "https://mobprog.webug.se/json-api?login=a22hanfa";

    private SharedPreferences sharedPreferences;
    private ArrayList<inputDecoder> items;  // Make items a class variable
    private RecyclerView recyclerView;  // Declare recyclerView as a class variable

    private String currentSortOrder;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences and other variables
        sharedPreferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
        items = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the saved sort order from SharedPreferences
        String savedSortOrder = sharedPreferences.getString("sortOrder", "name");

        // Perform JSON data retrieval
        JsonTask jsonTask = new JsonTask(this);
        jsonTask.execute(JSON_URL);

        // Initialize the About button
        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Initialize the Category button
        Button sortCategoryButton = findViewById(R.id.sort_category_button);
        sortCategoryButton.setOnClickListener(view -> {
            setSortOrder("category");
        });

        // Initialize the Location button
        Button sortLocationButton = findViewById(R.id.sort_location_button);
        sortLocationButton.setOnClickListener(view -> {
            setSortOrder("location");
        });

        // Initialize the Name button
        Button sortNameButton = findViewById(R.id.sort_name_button);
        sortNameButton.setOnClickListener(view -> {
            setSortOrder("name");
        });
    }


    public void onPostExecute(String json) {
        runOnUiThread(() -> {
            try {
                // Parse the received JSON data
                JSONArray jsonArray = new JSONArray(json);
                items = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    items.add(new inputDecoder(jsonArray.getJSONObject(i)));
                }

                // Create and set up the RecyclerViewAdapter
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, items, item -> {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("ID", item.getID());
                    intent.putExtra("Name", item.getName());
                    intent.putExtra("Location", item.getLocation());
                    intent.putExtra("Category", item.getCategory());
                    intent.putExtra("Auxdata", item.getAuxdata());
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);

                // Check if the current sort order is not null and apply the sort order
                if (currentSortOrder != null) {
                    sortAndFilterList();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse JSON: " + json, e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void sortAndFilterList() {
        // Retrieve the current sort order and filters from SharedPreferences
        String sortOrder = sharedPreferences.getString("sortOrder", "name");
        String filterCategory = sharedPreferences.getString("filterCategory", "all");
        String filterLocation = sharedPreferences.getString("filterLocation", "all");

        // Create a new list for sorted and filtered items
        ArrayList<inputDecoder> sortedAndFilteredList = new ArrayList<>();

        // Apply the filters and add matching items to the new list
        for (inputDecoder item : items) {
            if ((filterCategory.equals("all") || item.getCategory().equals(filterCategory)) &&
                    (filterLocation.equals("all") || item.getLocation().equals(filterLocation))) {
                sortedAndFilteredList.add(item);
            }
        }

        // Create the appropriate comparator based on the sort order
        Comparator<inputDecoder> comparator;
        switch (sortOrder) {
            case "category":
                comparator = (item1, item2) -> item1.getCategory().compareTo(item2.getCategory());
                break;
            case "location":
                comparator = (item1, item2) -> item1.getLocation().compareTo(item2.getLocation());
                break;
            default:
                comparator = (item1, item2) -> item1.getName().compareTo(item2.getName());
                break;
        }

        // Sort the new list
        Collections.sort(sortedAndFilteredList, comparator);

        // Create a new RecyclerViewAdapter with the sorted and filtered list
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, sortedAndFilteredList, item -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("ID", item.getID());
            intent.putExtra("Name", item.getName());
            intent.putExtra("Location", item.getLocation());
            intent.putExtra("Category", item.getCategory());
            intent.putExtra("Auxdata", item.getAuxdata());
            startActivity(intent);
        });
        // Set the new adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
    }

    public void setSortOrder(String sortOrder) {
        // Update the current sort order in SharedPreferences
        currentSortOrder = sortOrder;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sortOrder", currentSortOrder);
        editor.apply();

        // Apply the new sort order to the list
        sortAndFilterList();
    }


    public void setFilterCategory(String filterCategory) {

        // Update the filter category in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("filterCategory", filterCategory);
        editor.apply();

        // Apply the new filter to the list
        sortAndFilterList();
    }

    public void setFilterLocation(String filterLocation) {

        // Update the filter location in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("filterLocation", filterLocation);
        editor.apply();

        // Apply the new filter to the list
        sortAndFilterList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve the saved sort order from SharedPreferences
        String savedSortOrder = sharedPreferences.getString("sortOrder", "name");
        currentSortOrder = savedSortOrder;

        // Check if the RecyclerView has an adapter and apply the sort order
        if (recyclerView.getAdapter() != null) {
            sortAndFilterList();
        }
        Log.d(TAG, "Retrieved sortOrder from SharedPreferences: " + currentSortOrder);
    }
}
