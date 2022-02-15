package com.example.olioht.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.olioht.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Default values for API input parameters (finnish average, in grams)
    TextView nameTv;
    int beef = 346, fish = 232, pork = 596, dairy = 2000, cheese = 464,
            egg = 214, winterSalad = 1142, resturantSpending = 12, rice = 108;
    //Input values for the API (%)
    int beefFinal, fishFinal, porkFinal, dairyFinal, cheeseFinal, eggFinal,
            winterSaladFinal, resturantSpendingFinal, riceFinal;

    String diet = "";
    Boolean carbonPref = null;
    MaterialButton calculateBtn;
    Spinner carbonSpinner, dietSpinner;

    //Parameters for the API input
    TextInputEditText beefLevel, fishLevel, porkLevel, dairyLevel, cheeseLevel,
            riceLevel, eggLevel, winterSaladlevel, resturantSpendingLevel;

    Pref pref ;

    @Override
    //On first activity call onCreate() creates the activity because savedInstanceState is null
    //Saves the state of the application to savedInstanceState when new orientation is created by calling onCreate()
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the layout of the main view
        pref = new Pref(this);
        carbonSpinner = findViewById(R.id.lowCarbonSpinner);
        dietSpinner = (Spinner) findViewById(R.id.dietSpinner);
        diet = dietSpinner.getSelectedItem().toString();

        beefLevel = findViewById(R.id.txt_beefLevel);
        fishLevel = findViewById(R.id.txt_fishLevel);
        porkLevel = findViewById(R.id.txt_porkPoultryLevel);
        dairyLevel = findViewById(R.id.txt_dairyLevel);
        cheeseLevel = findViewById(R.id.txt_cheeseLevel);
        riceLevel = findViewById(R.id.txt_riceLevel);
        eggLevel = findViewById(R.id.txt_eggLevel);
        winterSaladlevel = findViewById(R.id.txt_winterSaladLevel);
        resturantSpendingLevel = findViewById(R.id.txt_restaurantSpending);

        Log.d("TAG", "Selected Diet Item Start :" + diet);
        spinnerListener();
        nameTv = findViewById(R.id.name_tv);
        calculateBtn = findViewById(R.id.btn_calculate);
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeValuesForAPI();
                requestAPI();
            }
        });

    }


    private void requestAPI() {
        String mainUrl = "https://ilmastodieetti.ymparisto.fi/ilmastodieetti/calculatorapi/v1/FoodCalculator?";
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(mainUrl);
        // for diet
        urlBuilder.append("query.diet=").append(diet);
        // for carbon preference
        if (carbonPref != null) {
            urlBuilder.append("&query.lowCarbonPreference=").append(carbonPref.toString());
        }
        // beef
        urlBuilder.append("&query.beefLevel=").append(beefFinal);
        // fish
        urlBuilder.append("&query.fishLevel=").append(fishFinal);
        // pork or poultry
        urlBuilder.append("&query.porkPoultryLevel=").append(porkFinal);
        // dairy
        urlBuilder.append("&query.dairyLevel=").append(dairyFinal);
        // cheese
        urlBuilder.append("&query.cheeseLevel=").append(cheeseFinal);
        //rice
        urlBuilder.append("&query.riceLevel=").append(riceFinal);
        //egg
        urlBuilder.append("&query.eggLevel=").append(eggFinal);
        //winter salad
        urlBuilder.append("&query.winterSaladLevel=").append(winterSaladFinal);
        //restaurant spending
        urlBuilder.append("&query.restaurantSpending=").append(resturantSpendingFinal);


        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("TAG", json.toString());

                            Log.d("TAG", "Meat : " + json.getString("Meat"));
                            Log.d("TAG", "Meat : " + json.getString("Dairy"));
                            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                            databaseHelper.storeResult(pref.getUserId(),response);
                            Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                            intent.putExtra("JSON", response.trim());
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error occurs :"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    //Setting functions for the options menu
    // e.g the sign-out button signs the user out, updates the information on Pref (Application state data)
    // and starts the SignInActivity
    // The history-button starts the ListViewActivity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signOut) {
            Pref pref = new Pref(MainActivity.this);
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
    //Setting the low-carbon preference and diet drop-down menus
    //Sets the value for carbonPref/diet parameters depending on the position.
    //Carbonpref in boolean, diet in String
    private void spinnerListener() {
        carbonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] carbon = getResources().getStringArray(R.array.yesNo_array);
                if (position == 0) {
                    carbonPref = null;
                } else if (position == 1) {
                    carbonPref = true;
                } else if (position == 2) {
                    carbonPref = false;
                }
                Log.d("TAG", "Selected Carbon Item :" + carbonPref);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dietSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] deitArray = getResources().getStringArray(R.array.diet_array);
                diet = dietSpinner.getSelectedItem().toString();
                Log.d("TAG", "Selected Diet Item Later :" + diet);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //Create API input (% of the finnish average) from the user input to the application (grams)
    //Empty input uses the default values which are the Finnish average
    void finalizeValuesForAPI() {
        if (beefLevel.getText().toString().equals("")) {
            double percentage = (0 / beef) * 100;
            int myLast = (int) percentage;
            beefFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + beefFinal);
        } else {
            double grams = Double.parseDouble(beefLevel.getText().toString());
            double percentage = (grams / beef) * 100;
            int myLast = (int) percentage;
            beefFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + beefFinal);
        }
        if (fishLevel.getText().toString().equals("")) {
            double percentage = (0 / fish) * 100;
            int myLast = (int) percentage;
            fishFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + fishFinal);

        } else {
            double grams = Double.parseDouble(fishLevel.getText().toString());
            double percentage = (grams / fish) * 100;
            int myLast = (int) percentage;
            fishFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + fishFinal);

        }
        if (porkLevel.getText().toString().equals("")) {
            double percentage = (0 / pork) * 100;
            int myLast = (int) percentage;
            porkFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + porkFinal);

        } else {
            double grams = Double.parseDouble(porkLevel.getText().toString());
            double percentage = (grams / pork) * 100;
            int myLast = (int) percentage;
            porkFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + porkFinal);

        }
        if (dairyLevel.getText().toString().equals("")) {
            double percentage = (0 / dairy) * 100;
            int myLast = (int) percentage;
            dairyFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + dairyFinal);

        } else {
            double grams = Double.parseDouble(dairyLevel.getText().toString());
            double percentage = (grams / dairy) * 100;
            int myLast = (int) percentage;
            dairyFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + dairyFinal);

        }
        if (cheeseLevel.getText().toString().equals("")) {
            double percentage = (0 / cheese) * 100;
            int myLast = (int) percentage;
            cheeseFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + cheeseFinal);

        } else {
            double grams = Double.parseDouble(cheeseLevel.getText().toString());
            double percentage = (grams / cheese) * 100;
            int myLast = (int) percentage;
            cheeseFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + cheeseFinal);

        }
        if (riceLevel.getText().toString().equals("")) {
            double percentage = (0 / rice) * 100;
            int myLast = (int) percentage;
            riceFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + riceFinal);
        } else {
            double grams = Double.parseDouble(riceLevel.getText().toString());
            double percentage = (grams / rice) * 100;
            int myLast = (int) percentage;
            riceFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + riceFinal);
        }
        if (eggLevel.getText().toString().equals("")) {
            double percentage = (0 / egg) * 100;
            int myLast = (int) percentage;
            eggFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + eggFinal);
        } else {
            double grams = Double.parseDouble(eggLevel.getText().toString());
            double percentage = (grams / egg) * 100;
            int myLast = (int) percentage;
            eggFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + eggFinal);

        }
        if (winterSaladlevel.getText().toString().equals("")) {
            double percentage = (0 / winterSalad) * 100;
            int myLast = (int) percentage;
            winterSaladFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + winterSaladFinal);
        } else {
            double grams = Double.parseDouble(winterSaladlevel.getText().toString());
            double percentage = (grams / winterSalad) * 100;
            int myLast = (int) percentage;
            winterSaladFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + winterSaladFinal);
        }
        if (resturantSpendingLevel.getText().toString().equals("")) {
            resturantSpendingFinal = resturantSpending;
            Log.d("TAG", "Beef Final Value In Percentage : " + resturantSpendingFinal);
        } else {
            double euros = Double.parseDouble(resturantSpendingLevel.getText().toString());
            int myLast = (int) euros;
            resturantSpendingFinal = myLast;
            Log.d("TAG", "Beef Final Value In Percentage : " + resturantSpendingFinal);

        }
    }
}