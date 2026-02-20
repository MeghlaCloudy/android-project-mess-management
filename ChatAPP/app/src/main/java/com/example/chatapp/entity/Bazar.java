package com.example.chatapp.entity;

public class Bazar {
    private int id;
    private String date;
    private String itemName;
    private double amount;
    private int paidByMemberId; // কে দিয়েছে (optional)

    public Bazar() {}

    public Bazar(int id, String date, String itemName, double amount, int paidByMemberId) {
        this.id = id;
        this.date = date;
        this.itemName = itemName;
        this.amount = amount;
        this.paidByMemberId = paidByMemberId;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getPaidByMemberId() { return paidByMemberId; }
    public void setPaidByMemberId(int paidByMemberId) { this.paidByMemberId = paidByMemberId; }
}