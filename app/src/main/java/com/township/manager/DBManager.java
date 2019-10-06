package com.township.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

public class DBManager {
    private SQLiteDatabase sqLiteDatabase;
    static final String DBName = "Township-Manager";
    static final String TabNameLogin = "Login";
    static final String ColUsername = "Username";
    static final String ColPassword = "Password";
    static final String ColFirstName = "First_Name";
    static final String ColLastName = "Last_Name";
    static final String ColPhone = "Phone";
    static final String ColEmail = "Email";
    static final String ColProfileUpdated = "Profile_Updated";
    static final String ColTownship = "Township";
    static final String ColWing = "Wing";
    static final String ColApartment = "Apartment";
    static final String ColDesignation = "Designation";
    static final String ColType = "Type";

    static final int DBVersion = 1;

    static final String TABLE_WINGS = "Wings";
    static final String TABLE_NOTICES = "Notices";
    static final String TABLE_NOTICE_WINGS = "NoticeWing";

    static final String CreateTabLogin = "CREATE TABLE IF NOT EXISTS " + TabNameLogin + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + ColUsername + " TEXT," + ColPassword + " TEXT," + ColFirstName + " TEXT," + ColLastName + " TEXT," + ColPhone + " TEXT," + ColEmail + " TEXT," + ColTownship + " TEXT," + ColWing + " TEXT," + ColApartment + " TEXT," + ColDesignation + " TEXT," + ColProfileUpdated + " INTEGER," + ColType + " TEXT);";

    static final String CreateWingsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_WINGS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, wing_id TEXT, name TEXT)";
    static final String CreateNoticesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTICES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, notice_id TEXT, posted_by_first_name TEXT, posted_by_last_name TEXT, posted_by_designation TEXT, title TEXT, description TEXT, timestamp TEXT)";
    static final String CreateNoticeWingsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTICE_WINGS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, notice_id TEXT, wing_id TEXT)";


    static class DatabaseHelperUser extends SQLiteOpenHelper {
        Context context;

        DatabaseHelperUser(Context context) {
            super(context, DBName, null, DBVersion);
            this.context = context;

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CreateTabLogin);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TabNameLogin);
            onCreate(sqLiteDatabase);
        }

    }

    public DBManager(Context context) {
        DatabaseHelperUser databaseHelperUser = new DatabaseHelperUser(context);
        sqLiteDatabase = databaseHelperUser.getWritableDatabase();

    }

    public long Insert(ContentValues values) {
        try {
            long id = sqLiteDatabase.insertOrThrow(TabNameLogin, null, values);
            return id;
        } catch (SQLException e) {
            Log.d("Insert", e.getMessage());
        }
        return 0;
    }

    public Cursor getDataLogin() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TabNameLogin, null);
        return cursor;
    }

    public void deleteAll() {
        sqLiteDatabase.execSQL("delete from " + TabNameLogin);
    }

}