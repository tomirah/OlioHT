package com.example.olioht.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import com.example.olioht.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// *******
//Class for creating a new user
// *******


public class SignUpActivity extends AppCompatActivity {

    TextInputEditText edEmail, edPass, edPass2;
    TextInputEditText edName, edPhone;
    MaterialButton btnSignUp, btnBackSignIn;
    private static final String TAG = "SignUpActivity";
    Handler handler = new Handler();

    //Setting requirements for a strong password: 12-20 characters, both upper- and lowercase letters
    //and one special character (@#$%.)
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%.]).{12,20})";

    long userAvailable = -1;
    @Override
    //On first activity call onCreate() creates the activity because savedInstanceState is null
    //Saves the state of the application to savedInstanceState when new orientation is created by calling onCreate()
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        listerners();
    }

    //Checking from the database whether the username already exists or not
    //If user doesn't exist, add user by databasehelper.insertuser()
    //databaseHelper.insertuser() returns id, if id = -1 the operation failed
    private void signUpListener() {

        User user = new User(edName.getText().toString(),edEmail.getText().toString(),edPass.getText().toString());
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if(userAvailable == 1){
            edEmail.setError("UserName Already Exist");
            Toast.makeText(SignUpActivity.this,"Username already taken ",Toast.LENGTH_SHORT).show();
        }
        else {
            Runnable signUp = new Runnable() {
                @Override
                public void run() {
                    long result = databaseHelper.insertUser(user);
                    if (result != -1) {
                        Toast.makeText(SignUpActivity.this, "User Registered ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), com.example.olioht.activities.SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        Toast.makeText(SignUpActivity.this, "User Registration Failed " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            };

            handler.postDelayed(signUp, 3000);
        }
    }
    //Check that each field is filled and that the password fits the requirements when btnSignUp is used
    //Else gives the associated error message
    private void listerners() {
        btnSignUp.setOnClickListener(view -> {

            if (edName.getText().toString().equals("")) {
                edName.setError("Required!");
                return;
            }
            if (edEmail.getText().toString().equals("")) {
                edEmail.setError("Required!");
                return;
            }
            if (edPass.getText().toString().equals("")) {
                edPass.setError("Required!");
                return;
            }
            if(edPass.getText().toString().length() < 8){
                edPass.setError("Password Must be over 8 Characters");
                return;
            }
            if(!isValidPassword(edPass.getText().toString().trim()))
            {
                edPass.setError("Password Criteria Not Matched");
                return;
            }

            if (edPass2.getText().toString().equals("")) {
                edPass2.setError("Required!");
                return;
            }

            if(edPass2.getText().toString().length() < 8){
                edPass2.setError("Password Must be 8 Character");
                return;
            }
            if(!isValidPassword(edPass2.getText().toString().trim()))
            {
                edPass2.setError("Password Criteria Not Matched");
                return;
            }

            if (edPass.getText().toString().equals(edPass2.getText().toString())) {
                signUpListener();
            } else {
                Toast.makeText(SignUpActivity.this, "Password must match!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackSignIn.setOnClickListener(view -> finish());
        edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){
                    if(s.toString().length() > 3) {
                        checkUserName(s.toString());
                    }
                }
            }
        });
    }

    //layout
    private void init() {

        edEmail = findViewById(R.id.txt_userName);
        edName = findViewById(R.id.txt_name);
        edPass = findViewById(R.id.txt_pass);
        edPass2 = findViewById(R.id.txt_pass_2);
        btnSignUp = findViewById(R.id.btn_signup);
        btnBackSignIn = findViewById(R.id.btn_signin_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Checking whether edpass and edpass2 (the password and re-type) match
    //return value in boolean
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    //Comparing the input username to database
    //if it exists give error and set userAvailable = 1
    //else set userAvailable = -1
    void checkUserName(String s){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        long isExisit = databaseHelper.checkExistingUserName(s);
        if(isExisit == 1){
            edEmail.setError("UserName Already Exist");
            userAvailable = 1;
        }
        else{
            userAvailable = -1;
        }
    }
}