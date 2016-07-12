package com.markelytics.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.markelytics.android.model.PartnerDetails;
import com.markelytics.android.model.SurveyDetail;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "markelytics";
    private static int History_Count = 0;


    // User Profile table
    private static final String TABLE_USERPROFILE = "userprofile";
    private static final String KEY_ID = "id";
    private static final String CONTACTS_EMAIL = "email";
    private static final String CONTACTS_FNAME = "firstName";
    private static final String CONTACTS_lNAME = "lastName";
    private static final String CONTACTS_GENDER = "gender";
    private static final String CONTACTS_BIRTHYEAR = "yrOfBirth";
    private static final String CONTACTS_STREETADD = "streetAddrs";
    private static final String CONTACTS_ZIP = "zipCode";
    private static final String CONTACTS_PHONE = "phone";
    private static final String CONTACTS_FREQUENCY = "surveyFreq";

    // Surveys table  id=1 for Recent Survey and id=2 for Available Survey
    private static final String TABLE_SURVEYS = "surveys";
    private static final String SURVEY_PRIMARY_ID = "id";
    private static final String SURVEY_CAT_ID = "cat_id";
    private static final String SURVEY_ID = "survey_id";
    private static final String SURVEY_DATE = "date";
    private static final String SURVEY_DESCRIPTION = "description";
    private static final String SURVEY_POINTS = "points";
    private static final String NEW_SURVEY_POINTS = "new_points";
    private static final String SURVEY_LOI = "survey_loi";
    private static final String SURVEY_URL = "survey_url";

    //Tabel for Redeemption History
    private static final String TABLE_HISTORY = "history";
    private static final String HISTORY_PRIMARY_ID = "id";
    private static final String HISTORY_DATE = "date";
    private static final String HISTORY_DESCRIPTION = "description";
    private static final String HISTORY_POINTS = "points";

    // table for further profiling

    //Tabel for Redeemption History
    private static final String TABLE_FURTHER = "further";
    private static final String FURTHER_PRIMARY_ID = "p_id";
    private static final String FURTHER_ID = "id";
    private static final String FURTHER_NAME = "name";
    private static final String FURTHER_STATUS = "status";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("ganesh", "database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERPROFILE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + CONTACTS_EMAIL + " TEXT,"
                + CONTACTS_FNAME + " TEXT," + CONTACTS_lNAME + " TEXT," + CONTACTS_GENDER + " TEXT," + CONTACTS_BIRTHYEAR + " TEXT," + CONTACTS_STREETADD + " TEXT," + CONTACTS_ZIP + " TEXT," + CONTACTS_PHONE + " TEXT," + CONTACTS_FREQUENCY + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_SURVEY_TABLE = "CREATE TABLE " + TABLE_SURVEYS + "("
                + SURVEY_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SURVEY_CAT_ID + " INTEGER," +SURVEY_ID + " TEXT,"
                + SURVEY_DATE + " TEXT," + SURVEY_DESCRIPTION + " TEXT," + SURVEY_POINTS + " INTEGER," + NEW_SURVEY_POINTS + " TEXT,"+ SURVEY_LOI + " TEXT," + SURVEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_SURVEY_TABLE);

        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + HISTORY_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +HISTORY_DATE + " TEXT,"
                + HISTORY_DESCRIPTION + " TEXT," + HISTORY_POINTS + " INTEGER" + ")";

        db.execSQL(CREATE_HISTORY_TABLE);

        String CREATE_FURTHER_TABLE = "CREATE TABLE " + TABLE_FURTHER + "("
                + FURTHER_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +FURTHER_ID + " TEXT,"
                + FURTHER_NAME + " TEXT," + FURTHER_STATUS + " TEXT" + ")";

        db.execSQL(CREATE_FURTHER_TABLE);

        Log.e("ganesh", "table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERPROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FURTHER);
        onCreate(db);
    }

    public void Add_Profile(PartnerDetails profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, 1);
        values.put(CONTACTS_FNAME, profile.getFirstName());
        values.put(CONTACTS_lNAME, profile.getLastName());
        values.put(CONTACTS_EMAIL, profile.getEmail());
        values.put(CONTACTS_GENDER, profile.getGender());
        values.put(CONTACTS_BIRTHYEAR, profile.getDob());
        values.put(CONTACTS_STREETADD, profile.getAddress());
        values.put(CONTACTS_ZIP, profile.getZipCode());
        values.put(CONTACTS_PHONE, profile.getPhone());
        values.put(CONTACTS_FREQUENCY, profile.getSurveyFreq());

        db.insert(TABLE_USERPROFILE, null, values);
        db.close(); // Closing database connection
    }

    public void Delete_Profile(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERPROFILE, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public PartnerDetails Get_Profile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERPROFILE, new String[]{KEY_ID,
                        CONTACTS_FNAME, CONTACTS_lNAME, CONTACTS_EMAIL, CONTACTS_GENDER, CONTACTS_BIRTHYEAR, CONTACTS_STREETADD, CONTACTS_ZIP, CONTACTS_PHONE, CONTACTS_FREQUENCY}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PartnerDetails profile = new PartnerDetails();
        profile.setFirstName(cursor.getString(cursor.getColumnIndex(CONTACTS_FNAME)));
        profile.setLastName(cursor.getString(cursor.getColumnIndex(CONTACTS_lNAME)));
        profile.setEmail(cursor.getString(cursor.getColumnIndex(CONTACTS_EMAIL)));
        profile.setAddress(cursor.getString(cursor.getColumnIndex(CONTACTS_STREETADD)));
        profile.setDob(cursor.getString(cursor.getColumnIndex(CONTACTS_BIRTHYEAR)));
        profile.setGender(cursor.getString(cursor.getColumnIndex(CONTACTS_GENDER)));
        profile.setZipCode(cursor.getString(cursor.getColumnIndex(CONTACTS_ZIP)));
        profile.setPhone(cursor.getString(cursor.getColumnIndex(CONTACTS_PHONE)));
        profile.setSurveyFreq(cursor.getString(cursor.getColumnIndex(CONTACTS_FREQUENCY)));

        cursor.close();
        db.close();

        return profile;
    }

    public int isProfileAvailable() {
        String countQuery = "SELECT  * FROM " + TABLE_USERPROFILE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int j = cursor.getCount();
        cursor.close();
        return j;
    }

    public Boolean isDataChanged(PartnerDetails profile) {
        Boolean result = true;
//        String WHERE = KEY_ID+" = 1 "+CONTACTS_FNAME + " = " + profile.getFirstName() + " AND " + CONTACTS_lNAME + " = " + profile.getLastName() + " AND " + CONTACTS_EMAIL + " = " + profile.getEmail() + " AND " + CONTACTS_STREETADD + " = " + profile.getAddress() + " AND " + CONTACTS_ZIP + " = " + profile.getZipCode() + " AND " + CONTACTS_FREQUENCY + " = " + profile.getSurveyFreq() + " AND " + CONTACTS_GENDER + " = " + profile.getGender() + " AND " + CONTACTS_BIRTHYEAR + " = " + profile.getDob() + " AND " + CONTACTS_PHONE + " = " + profile.getPhone();
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_USERPROFILE, new String[]{KEY_ID}, WHERE, null, null, null, null);


        Cursor cursor = db.query(TABLE_USERPROFILE, new String[]{KEY_ID}, KEY_ID + " =? AND " +CONTACTS_FNAME + " =? AND " + CONTACTS_lNAME + " =? AND " + CONTACTS_EMAIL + " =? AND " + CONTACTS_STREETADD + " =? AND " + CONTACTS_ZIP + " =? AND " + CONTACTS_FREQUENCY + " =?  AND " + CONTACTS_GENDER + " =? AND " + CONTACTS_BIRTHYEAR + " =? AND " + CONTACTS_PHONE + " =? " ,new String[]{String.valueOf("1"),profile.getFirstName(),profile.getLastName(),profile.getEmail(),profile.getAddress(),profile.getZipCode(),profile.getSurveyFreq(),profile.getGender(),profile.getDob(),profile.getPhone()}, null, null, null, null);


//        Cursor cursor = db.query(TABLE_USERPROFILE, new String[]{KEY_ID,
//                        CONTACTS_FNAME, CONTACTS_lNAME, CONTACTS_EMAIL, CONTACTS_GENDER, CONTACTS_BIRTHYEAR, CONTACTS_STREETADD, CONTACTS_ZIP, CONTACTS_PHONE, CONTACTS_FREQUENCY}, KEY_ID + "=?",
//                new String[]{String.valueOf("1")}, null, null, null, null);

        if (cursor != null) {
            if(cursor.getCount() > 0){
                result = false;
            };

        }
        cursor.close();
        db.close();
        return result;
    }
    ///Surveys table functions

    public void addSurvey(ArrayList<SurveyDetail> survey, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0; i<survey.size(); i++){
            SurveyDetail suv = survey.get(i);
            ContentValues values = new ContentValues();
            values.put(SURVEY_CAT_ID, id);
            values.put(SURVEY_ID, suv.getYrSurveyId());
            values.put(SURVEY_DATE, suv.getDate());
            values.put(SURVEY_DESCRIPTION, suv.getDescrption());
            values.put(SURVEY_POINTS, suv.getPoints());
            values.put(NEW_SURVEY_POINTS, suv.getYrSrvyPts());
            values.put(SURVEY_LOI, suv.getYrSrvyLoi());
            values.put(SURVEY_URL, suv.getYrSrvyUrl());
            db.insert(TABLE_SURVEYS, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteSurvey(int cat_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SURVEYS, SURVEY_CAT_ID + " = ?",
                new String[]{String.valueOf(cat_id)});
        db.close();
    }

    public ArrayList<SurveyDetail> Get_All_Surveys(int cat_id) {
        ArrayList<SurveyDetail> mysurveylist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SURVEYS, new String[]{SURVEY_ID,
                        SURVEY_DATE, SURVEY_DESCRIPTION, SURVEY_POINTS, NEW_SURVEY_POINTS, SURVEY_LOI, SURVEY_URL}, SURVEY_CAT_ID + "=?",
                new String[]{String.valueOf(cat_id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                SurveyDetail survey = new SurveyDetail();
                survey.setYrSurveyId(cursor.getString(cursor.getColumnIndex(SURVEY_ID)));
                survey.setDate(cursor.getString(cursor.getColumnIndex(SURVEY_DATE)));
                survey.setDescrption(cursor.getString(cursor.getColumnIndex(SURVEY_DESCRIPTION)));
                survey.setPoints(cursor.getInt(cursor.getColumnIndex(SURVEY_POINTS)));
                survey.setYrSrvyPts(cursor.getString(cursor.getColumnIndex(NEW_SURVEY_POINTS)));
                survey.setYrSrvyLoi(cursor.getString(cursor.getColumnIndex(SURVEY_LOI)));
                survey.setYrSrvyUrl(cursor.getString(cursor.getColumnIndex(SURVEY_URL)));
                mysurveylist.add(survey);
                cursor.moveToNext();
            }
        }

        if(cursor != null){
            cursor.close();
        }
        db.close();

        return mysurveylist;
    }

    public int getSurveyCount(int cat_id){
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SURVEYS, new String[]{SURVEY_ID}, SURVEY_CAT_ID + "=?",
                new String[]{String.valueOf(cat_id)}, null, null, null, null);

        if(cursor != null){
            count = cursor.getCount();
        }
        if(cursor != null){
            cursor.close();
        }
        db.close();
        return count;

    }


    ///////////////////////////////////HISTORY TABLE FUNCTIONS////

    public void addHistory(ArrayList<SurveyDetail> survey){
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0; i<survey.size(); i++){
            SurveyDetail suv = survey.get(i);
            ContentValues values = new ContentValues();
            values.put(HISTORY_DATE, suv.getDate());
            values.put(HISTORY_DESCRIPTION, suv.getDescrption());
            values.put(HISTORY_POINTS, suv.getPoints());
            db.insert(TABLE_HISTORY, null, values);
        }
        db.close(); // Closing database connection
    }
    public ArrayList<SurveyDetail> getAllHistory (){
        ArrayList<SurveyDetail> mysurveylist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_HISTORY, null);
        if (cursor != null) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                SurveyDetail survey = new SurveyDetail();
                survey.setDate(cursor.getString(cursor.getColumnIndex(HISTORY_DATE)));
                survey.setDescrption(cursor.getString(cursor.getColumnIndex(HISTORY_DESCRIPTION)));
                survey.setPoints(cursor.getInt(cursor.getColumnIndex(HISTORY_POINTS)));
                mysurveylist.add(survey);
                cursor.moveToNext();
            }
        }

        if(cursor != null){
            cursor.close();
        }
        db.close();

        return mysurveylist;
    }
    public void Delete_History() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_HISTORY);
        db.close();
    }

    public int getHistoryCount(){
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select "+HISTORY_POINTS+" from " + TABLE_HISTORY, null);

        if(cursor != null){
            count = cursor.getCount();
        }
        if(cursor != null){
            cursor.close();
        }
        db.close();
        return count;

    }

    ///////////////further table

    public void addFurther(ArrayList<PartnerDetails> survey){
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0; i<survey.size(); i++){
            PartnerDetails suv = survey.get(i);
            ContentValues values = new ContentValues();
            values.put(FURTHER_ID, suv.getProfilingId());
            values.put(FURTHER_NAME, suv.getProfilingName());
            values.put(FURTHER_STATUS, suv.getProfilingStatus());
            db.insert(TABLE_FURTHER, null, values);
        }
        db.close(); // Closing database connection
    }

    public ArrayList<PartnerDetails> getAllFurther (){
        ArrayList<PartnerDetails> mysurveylist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_FURTHER, null);
        if (cursor != null) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                PartnerDetails survey = new PartnerDetails();
                survey.setProfilingId(cursor.getString(cursor.getColumnIndex(FURTHER_ID)));
                survey.setProfilingName(cursor.getString(cursor.getColumnIndex(FURTHER_NAME)));
                survey.setProfilingStatus(cursor.getString(cursor.getColumnIndex(FURTHER_STATUS)));
                mysurveylist.add(survey);
                cursor.moveToNext();
            }
        }

        if(cursor != null){
            cursor.close();
        }
        db.close();

        return mysurveylist;
    }

    public void Delete_FURTHER() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_FURTHER);
        db.close();
    }

    public void Delete_ALL(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_FURTHER);
        db.execSQL("DELETE FROM "+TABLE_HISTORY);
        db.execSQL("DELETE FROM "+TABLE_USERPROFILE);
        db.execSQL("DELETE FROM "+TABLE_SURVEYS);
        db.close();
    }

}