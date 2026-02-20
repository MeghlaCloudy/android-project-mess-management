package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatapp.R;
import com.example.chatapp.entity.Member;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddMemberBottomSheet extends BottomSheetDialogFragment {

    private EditText etName, etPhone;
    private Button btnSave;
    private OnMemberAddedListener listener;

    public interface OnMemberAddedListener {
        void onMemberAdded(Member member);
    }

    public void setOnMemberAddedListener(OnMemberAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        btnSave = view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "নাম দিন", Toast.LENGTH_SHORT).show();
                return;
            }

            Member newMember = new Member(0, name, phone);
            if (listener != null) {
                listener.onMemberAdded(newMember);
            }
            dismiss();
        });
    }
}