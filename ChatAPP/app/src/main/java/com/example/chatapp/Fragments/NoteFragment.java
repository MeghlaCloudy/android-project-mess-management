package com.example.chatapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;

public class NoteFragment extends Fragment {

    private EditText etNote;
    private Button btnSave;
    private static final String PREFS_NAME = "MyNotesPrefs";
    private static final String KEY_NOTE = "my_note_text";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNote = view.findViewById(R.id.etNote);
        btnSave = view.findViewById(R.id.btnSave);

        // লোড করো আগের লেখা
        loadSavedNote();

        btnSave.setOnClickListener(v -> {
            String text = etNote.getText().toString().trim();
            saveNote(text);
            Toast.makeText(requireContext(), "Note saved!", Toast.LENGTH_SHORT).show();

            // Save-এর পর View Saved Note পেজে যাও
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new NoteViewFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void saveNote(String text) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NOTE, text);
        editor.apply();
    }

    private void loadSavedNote() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedText = prefs.getString(KEY_NOTE, "");
        etNote.setText(savedText);
    }
}