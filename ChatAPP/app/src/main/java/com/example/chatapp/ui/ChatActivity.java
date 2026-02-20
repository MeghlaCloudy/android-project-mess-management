package com.example.chatapp.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.entity.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ListView listMessages;
    private EditText etMessage;
    private Button btnSend;
    private ImageButton btnBack;
    private TextView tvChatName;

    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private String receiverUid;
    private String receiverName;

    private String senderUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        listMessages = findViewById(R.id.listMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvChatName = findViewById(R.id.tvChatName);

        // Get data from intent
        receiverUid = getIntent().getStringExtra("uid");
        receiverName = getIntent().getStringExtra("name");
        tvChatName.setText(receiverName);

        senderUid = FirebaseAuth.getInstance().getUid();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Setup adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listMessages.setAdapter(adapter);

        // Load messages
        loadMessages();

        // Send button
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        String chatId = getChatId(senderUid, receiverUid);

        FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        adapter.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Message m = ds.getValue(Message.class);
                            if (m != null) {
                                messages.add(m);
                                String display = (m.senderUid.equals(senderUid) ? "You: " : receiverName + ": ") + m.text;
                                adapter.add(display);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        listMessages.setSelection(adapter.getCount() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String chatId = getChatId(senderUid, receiverUid);
        String msgId = FirebaseDatabase.getInstance().getReference("chats").child(chatId).push().getKey();

        Message m = new Message(senderUid, receiverUid, text, System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId)
                .child(msgId)
                .setValue(m);

        etMessage.setText("");
    }

    private String getChatId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
}
