package com.example.olioht.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.olioht.R;
import java.util.ArrayList;

/*
Using recyclerview to list the calculation result history. Basically data is supplied to
recyclerview, which then creates new elements as they are needed.

Arraylist stores data, which is gathered from SQL database by (saved state) userID using the
databaseHelper.getAllResultsData()

Adapter associates data with the viewholder views
 */

public class ListViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> arrayList;
    CustomRecyclerViewAdapter customRecyclerViewAdapter;
    DatabaseHelper databaseHelper;
    Pref pref;
    @Override

    //On first activity call onCreate() creates the activity because savedInstanceState is null
    //Saves the state of the application to savedInstanceState when new orientation is created by calling onCreate()
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        recyclerView = findViewById(R.id.recycler_View);
        arrayList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        pref = new Pref(this);

        arrayList = databaseHelper.getAllResultsData(pref.getUserId());
        Log.d("TAG","array "+arrayList);
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this,arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customRecyclerViewAdapter);
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customRecyclerViewAdapter.notifyDataSetChanged();

            }
        });
    }
}