package com.example.medlo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "MedLo.db";
    public static final int DB_VERSION = 1;

    // User table
    public static final String TABLE_USER = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_NUMBER = "number";

    // Medicine table
    public static final String TABLE_MEDICINE = "medicines";
    public static final String COL_MED_ID = "id";
    public static final String COL_MED_NAME = "medicine_name";
    public static final String COL_MED_TIME = "alarm_time";
    public static final String COL_USER_EMAIL_REF = "user_email";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_NUMBER + " TEXT)";

        String createMedicineTable = "CREATE TABLE " + TABLE_MEDICINE + " (" +
                COL_MED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MED_NAME + " TEXT, " +
                COL_MED_TIME + " TEXT, " +
                COL_USER_EMAIL_REF + " TEXT, " +
                "FOREIGN KEY(" + COL_USER_EMAIL_REF + ") REFERENCES " + TABLE_USER + "(" + COL_USER_EMAIL + "))";

        db.execSQL(createUserTable);
        db.execSQL(createMedicineTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Insert user
    public boolean insertUser(String name, String email, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_NUMBER, number);

        long result = db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    // Insert medicine
    public boolean insertMedicine(String medicineName, String alarmTime, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MED_NAME, medicineName);
        values.put(COL_MED_TIME, alarmTime);
        values.put(COL_USER_EMAIL_REF, userEmail);

        long result = db.insert(TABLE_MEDICINE, null, values);
        return result != -1;
    }

    // Get all info for home screen
    public Cursor getUserWithMedicines(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.name, u.email, u.number, m.medicine_name, m.alarm_time " +
                "FROM users u LEFT JOIN medicines m ON u.email = m.user_email " +
                "WHERE u.email = ?";
        return db.rawQuery(query, new String[]{email});
    }
    // Corrected: Fetch user by email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("number")); // fixed
            cursor.close();
            return new User(name, phone, email);
        }
        cursor.close();
        return null;
    }

    // Corrected: Get last medicine reminder for a user
    public Medicine getLastMedicineByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM medicines WHERE user_email = ? ORDER BY id DESC LIMIT 1", new String[]{email});

        if (cursor.moveToFirst()) {
            String medName = cursor.getString(cursor.getColumnIndexOrThrow("medicine_name")); // fixed
            String medTime = cursor.getString(cursor.getColumnIndexOrThrow("alarm_time"));     // fixed
            cursor.close();
            return new Medicine(medName, medTime);
        }
        cursor.close();
        return null;
    }

}
