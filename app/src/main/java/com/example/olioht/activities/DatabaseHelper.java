package com.example.olioht.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import com.example.olioht.activities.User;
import java.util.ArrayList;

/*

SQLiteOpenHelper is used to manage database creation and version management
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //Constants for db
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;

        public static final String DATABASE_NAME = "food-calculator-db";
        public static final String TABLE_USER = "users";
        public static final String COLUMN_USER_ID = "_id";
        public static final String COLUMN_USER_NAME = "name";
        public static final String COLUMN_USER_USERNAME = "username";
        public static final String COLUMN_USER_PASS = "password";



        public static final String TABLE_RESULT_ = "foodresult";
        public static final String COLUMN_RESULT_ID = "resul_id";
        public static final String COLUMN_RESULT_USER_ID = "result_user_id";
        public static final String COLUMN_RESULT_DATA = "result_data";
        

    private Context context = null;

    //A helper object to create, open, and/or manage a database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_NAME + " TEXT, "
            + COLUMN_USER_USERNAME + " TEXT NOT NULL, "
            + COLUMN_USER_PASS + " TEXT NOT NULL" + ")";

    private static final String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULT_ + " ("
            + COLUMN_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RESULT_USER_ID + " INTEGER, "
            + COLUMN_RESULT_DATA + " TEXT NOT NULL" + ")";

    @Override
    //Creating the tables for User and Results
    public void onCreate(SQLiteDatabase db) {


        try {
            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_RESULTS_TABLE);
        } catch (Exception e) {
            Log.d("TAG", "error to create table :" + e.getLocalizedMessage());

        }

    }

    @Override
    //Drop older table(s), if they exist, and then runs onCreate().
    //Called when database needs to be upgrades
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT_);
        onCreate(db);

    }
//Inserting user to the database (Name, Username, Password)
    public long insertUser(User user) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_NAME, user.getName());
        contentValues.put(COLUMN_USER_USERNAME, user.getUsername());
        contentValues.put(COLUMN_USER_PASS, user.getPassword());

        try {
            id = db.insertOrThrow(TABLE_USER, null, contentValues);
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }

        return id;
    }

//Checks that the input username and password combination can be found in DB
    public String checkUserInDatabase(User user){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String name = null;
        String selectUserFromDatabase = "SELECT "+ COLUMN_USER_NAME+" FROM " + TABLE_USER +" WHERE "
                + COLUMN_USER_USERNAME + " = '" + user.getUsername() + "' AND " + COLUMN_USER_PASS + " = '" + user.getPassword()+"' ";

        try {

            Log.d("DatabaseHelper","query :"+selectUserFromDatabase);
            cursor = db.rawQuery(selectUserFromDatabase,null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        String nameInRow = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
                        Log.d("TAG","grade is :"+nameInRow);
                        name = nameInRow;

                    } while (cursor.moveToNext());
                    return name;
                }
            }
        } catch (Exception e) {
            Log.d("TAG","error to get assignment :"+e.getLocalizedMessage());
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }

        return name;
    }
    //Takes username as input
    //Compares username to existing users in the DB in the table "TABLE_USER"
    //Return 1 if user is found, -1 if not found
    public long checkExistingUserName(String userName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String selectUserFromDatabase = "SELECT "+ COLUMN_USER_USERNAME+" FROM " + TABLE_USER +" WHERE "
                + COLUMN_USER_USERNAME + " = '" + userName + "' " ;
        try {
            Log.d("DatabaseHelper","query :"+selectUserFromDatabase);
            cursor = db.rawQuery(selectUserFromDatabase,null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                    } while (cursor.moveToNext());
                    return 1;
                }
            }
        } catch (Exception e) {
            Log.d("TAG","error to get assignment :"+e.getLocalizedMessage());
            return 1;
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }

        return -1;
    }
    //Takes username and password as input for the DB query
    //Compares UserName and Password to existing users
    //If user is found, returns UserID.
    public long checkUserIDInDatabase(User user){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long id = -1;
        String selectUserFromDatabase = "SELECT "+ COLUMN_USER_ID+" FROM " + TABLE_USER +" WHERE "
                + COLUMN_USER_USERNAME + " = '" + user.getUsername() + "' AND " + COLUMN_USER_PASS + " = '" + user.getPassword()+"' ";

        try {

            Log.d("DatabaseHelper","query :"+selectUserFromDatabase);
            cursor = db.rawQuery(selectUserFromDatabase,null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        int idRow = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                        id = idRow;

                    } while (cursor.moveToNext());
                    return id;
                }
            }
        } catch (Exception e) {
            Log.d("TAG","error to get assignment :"+e.getLocalizedMessage());
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }

        return id;
    }

    //Storing values from the API output to a specific UserID
    public long storeResult(int id, String data){
        SQLiteDatabase database = this.getWritableDatabase();
        long result = -1;

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RESULT_USER_ID, id);
        contentValues.put(COLUMN_RESULT_DATA, data);

        try {
            result = database.insertOrThrow(TABLE_RESULT_, null, contentValues);
        } catch (SQLiteException e) {
            Log.d("TAG","error to save result data's :"+e.getLocalizedMessage());

        } finally {
            database.close();
        }

        return result;
    }
    //Retrieves all the data of the userID from the database
    public ArrayList<String> getAllResultsData(int userId){

        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = null;
        String selectResultData = "SELECT * FROM " + TABLE_RESULT_ +" WHERE "
                + COLUMN_RESULT_USER_ID + " = " + userId;
        Log.d("TAG","query for result :"+selectResultData);

        try{
            cursor = database.rawQuery(selectResultData,null);

            if (cursor != null) {
                if (cursor.moveToFirst()){

                    do{
                        String resultData = cursor.getString(cursor.getColumnIndex(COLUMN_RESULT_DATA));
                        arrayList.add(resultData);
                        Log.d("TAG","result now :"+resultData);

                    }
                    while (cursor.moveToNext());
                }
                return arrayList;
            }
        }
        catch (Exception e){
            Log.d("TAG","error to get result data's :"+e.getLocalizedMessage());
        }
        finally {
            database.close();
        }

        return arrayList;
    }
}