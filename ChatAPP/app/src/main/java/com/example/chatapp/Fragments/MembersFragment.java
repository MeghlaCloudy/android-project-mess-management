package com.example.chatapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.MemberAdapter;
import com.example.chatapp.db.DatabaseHelper;
import com.example.chatapp.entity.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MembersFragment extends Fragment {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private ArrayList<Member> memberList;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAdd;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = requireContext();

        recyclerView = view.findViewById(R.id.recyclerViewMembers);
        fabAdd = view.findViewById(R.id.fabAddMember);

        dbHelper = new DatabaseHelper(mContext);

        memberList = new ArrayList<>();
        adapter = new MemberAdapter(mContext, memberList, new MemberAdapter.MemberClickListener() {
            @Override
            public void onMemberClick(Member member) {
                Toast.makeText(mContext, "Clicked: " + member.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(Member member) {
                EditMemberBottomSheet bottomSheet = EditMemberBottomSheet.newInstance(member);
                bottomSheet.setOnMemberUpdatedListener(updatedMember -> {
                    boolean success = dbHelper.updateMember(
                            updatedMember.getId(),
                            updatedMember.getName(),
                            updatedMember.getPhone()
                    );
                    if (success) {
                        loadMembers();
                        Toast.makeText(mContext, "মেম্বার আপডেট হয়েছে", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheet.show(getParentFragmentManager(), "edit_member");
            }

            @Override
            public void onDeleteClick(Member member, int position) {
                boolean deleted = dbHelper.deleteMember(member.getId());
                if (deleted) {
                    adapter.removeMember(position);
                    Toast.makeText(mContext, "মেম্বার ডিলিট হয়েছে", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "ডিলিটে সমস্যা", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);

        loadMembers();

        fabAdd.setOnClickListener(v -> showAddMemberBottomSheet());
    }

    private void loadMembers() {
        memberList.clear();
        memberList.addAll(dbHelper.getAllMembers());
        adapter.notifyDataSetChanged();

        if (memberList.isEmpty()) {
            Toast.makeText(mContext, "কোনো মেম্বার নেই। FAB দিয়ে যোগ করুন", Toast.LENGTH_LONG).show();
        }
    }

    private void showAddMemberBottomSheet() {
        AddMemberBottomSheet bottomSheet = new AddMemberBottomSheet();
        bottomSheet.setOnMemberAddedListener(newMember -> {
            long id = dbHelper.addMember(newMember.getName(), newMember.getPhone());
            if (id > 0) {
                loadMembers();
                Toast.makeText(mContext, "মেম্বার যোগ হয়েছে", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheet.show(getParentFragmentManager(), "add_member");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMembers(); // ফিরে এলে আবার লোড
    }
}