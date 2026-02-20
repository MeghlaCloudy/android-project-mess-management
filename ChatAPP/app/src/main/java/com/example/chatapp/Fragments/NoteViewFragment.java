package com.example.chatapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;

public class NoteViewFragment extends Fragment {

    private TextView tvSavedNote;
    private static final String PREFS_NAME = "MyNotesPrefs";
    private static final String KEY_NOTE = "my_note_text";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSavedNote = view.findViewById(R.id.tvSavedNote);

        // লোড করো সেভ করা নোট
        String savedText = loadSavedNote();
        if (savedText.isEmpty()) {
            tvSavedNote.setText("কোনো নোট সেভ করা নেই।\nNote পেজে গিয়ে লিখে সেভ করুন।");
        } else {
            tvSavedNote.setText(savedText);
        }

        // Optional: লং প্রেস করে কপি করার অপশন
        tvSavedNote.setOnLongClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Note", savedText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Note copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private String loadSavedNote() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NOTE, "");
    }
}