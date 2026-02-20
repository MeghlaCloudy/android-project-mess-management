package com.example.chatapp.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {

    public String text;
    public String senderUid;
    public String receiverUid;  // টাইপো ঠিক করা (recevier → receiver)
    public long timestamp;      // মেসেজের সময় (Firebase + অফলাইনের জন্য দরকারি)

    public Message() {}  // Firebase-এর জন্য দরকার

    public Message(String senderUid, String receiverUid, String text, long timestamp) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.text = text;
        this.timestamp = timestamp;
    }

    // অফলাইন/অনলাইনের জন্য সময় দেখানোর জন্য
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}