package com.example.chatapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chatapp.entity.Member;
import com.example.chatapp.entity.Meal;
import com.example.chatapp.entity.Bazar;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mess_db";
    private static final int DB_VERSION = 1;

    // Table Names
    private static final String TABLE_MEMBERS = "members";
    private static final String TABLE_MEALS = "meals";
    private static final String TABLE_BAZAR = "bazar";

    // Common column
    private static final String COL_ID = "id";

    // Members columns
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";

    // Meals columns
    private static final String COL_MEMBER_ID = "member_id";
    private static final String COL_DATE = "date"; // "2025-01-20"
    private static final String COL_BREAKFAST = "breakfast";
    private static final String COL_LUNCH = "lunch";
    private static final String COL_DINNER = "dinner";

    // Bazar columns
    private static final String COL_ITEM_NAME = "item_name";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_PAID_BY = "paid_by_member_id";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Members Table
        db.execSQL("CREATE TABLE " + TABLE_MEMBERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PHONE + " TEXT)");

        // Meals Table
        db.execSQL("CREATE TABLE " + TABLE_MEALS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEMBER_ID + " INTEGER NOT NULL, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_BREAKFAST + " INTEGER DEFAULT 0, " +
                COL_LUNCH + " INTEGER DEFAULT 0, " +
                COL_DINNER + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_MEMBER_ID + ") REFERENCES " + TABLE_MEMBERS + "(" + COL_ID + "))");

        // Bazar Table
        db.execSQL("CREATE TABLE " + TABLE_BAZAR + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_ITEM_NAME + " TEXT, " +
                COL_AMOUNT + " REAL NOT NULL, " +
                COL_PAID_BY + " INTEGER, " +
                "FOREIGN KEY(" + COL_PAID_BY + ") REFERENCES " + TABLE_MEMBERS + "(" + COL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAZAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        onCreate(db);
    }

    // ================== Members Operations ==================

    public long addMember(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_PHONE, phone.trim());
        long id = db.insert(TABLE_MEMBERS, null, values);
        db.close();
        return id;
    }

    public ArrayList<Member> getAllMembers() {
        ArrayList<Member> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMBERS, null, null, null, null, null, COL_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
                list.add(new Member(id, name, phone));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean updateMember(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_PHONE, phone.trim());
        int rows = db.update(TABLE_MEMBERS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_MEMBERS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public Member getMemberById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMBERS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Member member = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
            member = new Member(id, name, phone);
        }
        cursor.close();
        db.close();
        return member;
    }

    // ================== Meals Operations ==================

    public long addMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MEMBER_ID, meal.getMemberId());
        values.put(COL_DATE, meal.getDate());
        values.put(COL_BREAKFAST, meal.getBreakfast());
        values.put(COL_LUNCH, meal.getLunch());
        values.put(COL_DINNER, meal.getDinner());
        long id = db.insert(TABLE_MEALS, null, values);
        db.close();
        return id;
    }

    public List<Meal> getMealsByDate(String date) {
        List<Meal> meals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEALS, null, COL_DATE + "=?",
                new String[]{date}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                int memberId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MEMBER_ID));
                String mealDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                int breakfast = cursor.getInt(cursor.getColumnIndexOrThrow(COL_BREAKFAST));
                int lunch = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LUNCH));
                int dinner = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DINNER));

                meals.add(new Meal(id, memberId, mealDate, breakfast, lunch, dinner));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return meals;
    }

    public boolean deleteMeal(int mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_MEALS, COL_ID + "=?", new String[]{String.valueOf(mealId)});
        db.close();
        return rows > 0;
    }

    // ================== Bazar Operations ==================

    public long addBazar(Bazar bazar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, bazar.getDate());
        values.put(COL_ITEM_NAME, bazar.getItemName());
        values.put(COL_AMOUNT, bazar.getAmount());
        values.put(COL_PAID_BY, bazar.getPaidByMemberId());
        long id = db.insert(TABLE_BAZAR, null, values);
        db.close();
        return id;
    }

    public List<Bazar> getBazarByDate(String date) {
        List<Bazar> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BAZAR, null, COL_DATE + "=?",
                new String[]{date}, null, null, COL_DATE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String bDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                String item = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT));
                int paidBy = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAID_BY));
                list.add(new Bazar(id, bDate, item, amount, paidBy));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteBazar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_BAZAR, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // ================== Calculation Methods ==================

    // Total meals for all members in a date range
    public int getTotalMealBetween(String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_BREAKFAST + " + " + COL_LUNCH + " + " + COL_DINNER + ") as total " +
                        "FROM " + TABLE_MEALS + " WHERE " + COL_DATE + " BETWEEN ? AND ?",
                new String[]{startDate, endDate}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }

    // Total meals for a specific member in a date range
    public int getTotalMealByMember(int memberId, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_BREAKFAST + " + " + COL_LUNCH + " + " + COL_DINNER + ") as total " +
                        "FROM " + TABLE_MEALS + " WHERE " + COL_MEMBER_ID + " = ? AND " +
                        COL_DATE + " BETWEEN ? AND ?",
                new String[]{String.valueOf(memberId), startDate, endDate}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }

    // Total expenses in a date range
    public double getTotalBazarBetween(String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") as total FROM " + TABLE_BAZAR +
                        " WHERE " + COL_DATE + " BETWEEN ? AND ?",
                new String[]{startDate, endDate}
        );

        double total = 0.0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }

    // Total meals on a specific date
    public int getTotalMealByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_BREAKFAST + " + " + COL_LUNCH + " + " + COL_DINNER + ") as total " +
                        "FROM " + TABLE_MEALS + " WHERE " + COL_DATE + " = ?",
                new String[]{date}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }

    // Recent expenses (limited)
    public List<Bazar> getRecentBazar(int limit) {
        List<Bazar> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BAZAR, null, null, null, null, null,
                COL_DATE + " DESC", String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                String item = cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT));
                int paidBy = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PAID_BY));
                list.add(new Bazar(id, date, item, amount, paidBy));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}