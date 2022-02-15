package com.example.olioht.activities;

import android.content.Context;
import android.content.SharedPreferences;

/*
This class is used to storing preference data (saved state) from the application,
//e.g the account you've logged in on.
 */

public class Pref {

    public static final String PREF_NAME = "FoodCalculator";
    public static final String LOGIN_ = "login";
    public static final String USER_FULLNAME = "user_fullname";
    public static final String USER_ID = "user_id";



    int PRIVATE_MODE = 0;
    Context context;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    public Pref(Context context) {
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(PREF_NAME, this.PRIVATE_MODE);
        this.editor = this.sharedPreferences.edit();
        this.editor.apply();
    }

    public void setLogin(boolean isLogin){
        editor.putBoolean(LOGIN_,isLogin);
        editor.apply();
    }

    public boolean isLogin(){
        return  sharedPreferences.getBoolean(LOGIN_,false);
    }

    public void setUserFullname(String name){
        editor.putString(USER_FULLNAME,name);
        editor.apply();
    }
    public String getUserFullname(){
        return sharedPreferences.getString(USER_FULLNAME,"");
    }
    public void setUserId(int id){
        editor.putInt(USER_ID,id);
        editor.apply();
    }
    public int getUserId(){
        return sharedPreferences.getInt(USER_ID,0);
    }
}
