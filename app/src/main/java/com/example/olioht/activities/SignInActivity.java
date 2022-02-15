package com.example.olioht.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.example.olioht.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

//*******
//Class for signing in on an account
//******


public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignIn";
    MaterialButton btnSignIn, btnSignUp;
    TextInputEditText edEmail, edPass;
    Handler handler = new Handler();

    Pref pref;
    @Override
    //On first activity call onCreate() creates the activity because savedInstanceState is null
    //Saves the state of the application to savedInstanceState when new orientation is created by calling onCreate()
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        pref = new Pref(this);
        init();
        listeners();
    }

    //Requires username/password to be filled
    //Returns error message if empty
    private void listeners() {

        btnSignIn.setOnClickListener(view -> {
            if (edEmail.getText().toString().equals("")) {
                edEmail.setError("Required");
                return;
            }
            if (edPass.getText().toString().equals("")) {
                edPass.setError("Required");
                return;
            }
            authListener();
        });

        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void authListener() {
        //Confirms that the username exists and that the username/password combination matches the databaase.
        //If checkUserInDatabse() returns -1, the username was not found
        //If checkUserIDInDatabase() returns -1, the username/password combination was not found
        User user = new User("",edEmail.getText().toString(),edPass.getText().toString());
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Runnable signUp = new Runnable() {
            @Override
            public void run() {
                String result = databaseHelper.checkUserInDatabase(user);
                if(result != null){
                    pref.setLogin(true);
                    pref.setUserFullname(result);
                    long id = databaseHelper.checkUserIDInDatabase(user);

                    if(id != -1){
                        pref.setUserId((int) id);
                        Log.d("TAG","ID :"+id);
                    }
                    Toast.makeText(SignInActivity.this,"User Signed Successfully ",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), com.example.olioht.activities.MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);

                }else{
                    pref.setLogin(false);
                    Toast.makeText(SignInActivity.this,"User Not Found Please Check Username & Password ",Toast.LENGTH_SHORT).show();
                }
            }
        };

        handler.postDelayed(signUp,2000);
    }
//Layout
    private void init() {
        btnSignIn = findViewById(R.id.btn_signin);
        btnSignUp = findViewById(R.id.btn_signup);
        edEmail = findViewById(R.id.txt_userName);
        edPass = findViewById(R.id.txt_pass);
    }

    @Override
    //If an account is already logged in, clear any existing task before the activity (MainActivity) is started
    protected void onStart() {
        super.onStart();
        if(pref != null){
            if(pref.isLogin()){
                Intent intent = new Intent(getApplicationContext(), com.example.olioht.activities.MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}