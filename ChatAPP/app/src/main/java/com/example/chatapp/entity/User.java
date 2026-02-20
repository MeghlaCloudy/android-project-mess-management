package com.example.chatapp.entity;

public class User {

    public String uid;
    public String email;
    public String name;
    public String phone;

    public User() {}

    public User(String uid, String email, String name, String phone) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
}