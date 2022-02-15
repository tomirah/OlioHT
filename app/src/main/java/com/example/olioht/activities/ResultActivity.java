package com.example.olioht.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.olioht.R;
import org.json.JSONException;
import org.json.JSONObject;

//******
//Class for managing the results activity
//*******


public class ResultActivity extends AppCompatActivity {

    String response;
    TextView email_tv;
    Pref pref;
    TextView resultTv;
    @Override
    //On first activity call onCreate() creates the activity because savedInstanceState is null
    //Saves the state of the application to savedInstanceState when new orientation is created by calling onCreate()
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Setting up the layout of results screen.
        //Username from saved state
        //Results from API
        pref = new Pref(this);
        email_tv = findViewById(R.id.email_tv);
        resultTv = findViewById(R.id.result_tv);
        if(!pref.getUserFullname().equals("")){
            email_tv.setText(pref.getUserFullname());
        }

        response = getIntent().getStringExtra("JSON");
        JSONObject json = null;
        try {
            json = new JSONObject(response);
            Log.d("TAG", json.toString());

            Log.d("TAG", "Meat : " + json.getString("Meat"));
            Log.d("TAG", "Meat : " + json.getString("Dairy"));
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("<b>Dairy :</b> ").append(json.getInt("Dairy")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Meat : </b>").append(json.getInt("Meat")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Plant : </b>").append(json.getInt("Plant")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Restaurant : </b>").append(json.getInt("Restaurant")).append(" kg CO2 eq./year<br>");
            stringBuilder.append("<b>Total : </b>").append(json.getInt("Total")).append(" kg CO2 eq./year<br>");

            resultTv.setText(Html.fromHtml(stringBuilder.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

//Set functionalities for the buttons, e.g list_open_btn opens up the result/view history and starts
//the ListViewActivity

        ((findViewById(R.id.bk_btn))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((findViewById(R.id.list_open_btn))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.example.olioht.activities.ListViewActivity.class));
            }
        });

    }@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //Setting functions for the menu buttons, for example the sign-out button signs the user out and
    //updates the information on Pref (Application state data)
    // the history-button starts the ListViewActivity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signOut) {
            Pref pref = new Pref(ResultActivity.this);
            pref.setLogin(false);
            pref.setUserFullname("");
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        else if(id == R.id.listBtn){
            startActivity(new Intent(getApplicationContext(), com.example.olioht.activities.ListViewActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

}