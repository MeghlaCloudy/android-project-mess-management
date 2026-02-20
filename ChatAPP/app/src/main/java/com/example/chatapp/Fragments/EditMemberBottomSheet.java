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

public class EditMemberBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_MEMBER_ID = "member_id";
    private static final String ARG_MEMBER_NAME = "member_name";
    private static final String ARG_MEMBER_PHONE = "member_phone";

    private EditText etName, etPhone;
    private Button btnUpdate;
    private OnMemberUpdatedListener listener;

    // এই ইন্টারফেসটা শুধু একবারই থাকবে (দুইবার লিখেছিলি)
    public interface OnMemberUpdatedListener {
        void onMemberUpdated(Member updatedMember);  // এখানে পুরো Member অবজেক্ট পাঠাবে
    }

    public void setOnMemberUpdatedListener(OnMemberUpdatedListener listener) {
        this.listener = listener;
    }

    // newInstance মেথড — ঠিক আছে
    public static EditMemberBottomSheet newInstance(Member member) {
        EditMemberBottomSheet fragment = new EditMemberBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_MEMBER_ID, member.getId());
        args.putString(ARG_MEMBER_NAME, member.getName());
        args.putString(ARG_MEMBER_PHONE, member.getPhone());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_member_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.etMemberName);
        etPhone = view.findViewById(R.id.etMemberPhone);
        btnUpdate = view.findViewById(R.id.btnUpdateMember);

        // পুরানো ডাটা দেখানো
        if (getArguments() != null) {
            etName.setText(getArguments().getString(ARG_MEMBER_NAME, ""));
            etPhone.setText(getArguments().getString(ARG_MEMBER_PHONE, ""));
        }

        btnUpdate.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "নাম দিতে হবে", Toast.LENGTH_SHORT).show();
                return;
            }

            int memberId = getArguments() != null ? getArguments().getInt(ARG_MEMBER_ID) : -1;
            if (memberId == -1) {
                Toast.makeText(requireContext(), "এরর হয়েছে", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }

            // নতুন আপডেটেড মেম্বার অবজেক্ট তৈরি করা
            Member updatedMember = new Member(memberId, newName, newPhone);

            // লিসেনার দিয়ে MembersFragment-এ পাঠানো
            if (listener != null) {
                listener.onMemberUpdated(updatedMember);
            }

            Toast.makeText(requireContext(), "আপডেট হয়েছে!", Toast.LENGTH_SHORT).show();
            dismiss();  // বটমশিট বন্ধ
        });
    }
}