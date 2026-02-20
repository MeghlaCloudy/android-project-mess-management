package com.example.chatapp.entity;

public class Meal {
    private int id;
    private int memberId;
    private String date; // "2025-01-20" ফরম্যাটে
    private int breakfast; // 0 or 1
    private int lunch;     // 0 or 1
    private int dinner;    // 0 or 1

    public Meal() {}

    public Meal(int id, int memberId, String date, int breakfast, int lunch, int dinner) {
        this.id = id;
        this.memberId = memberId;
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getBreakfast() { return breakfast; }
    public void setBreakfast(int breakfast) { this.breakfast = breakfast; }

    public int getLunch() { return lunch; }
    public void setLunch(int lunch) { this.lunch = lunch; }

    public int getDinner() { return dinner; }
    public void setDinner(int dinner) { this.dinner = dinner; }
}