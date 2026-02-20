package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.entity.Member;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Member> memberList;
    private MemberClickListener listener;

    public interface MemberClickListener {
        void onMemberClick(Member member);
        void onEditClick(Member member);
        void onDeleteClick(Member member, int position);
    }

    public MemberAdapter(Context context, ArrayList<Member> memberList, MemberClickListener listener) {
        this.context = context;
        this.memberList = memberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = memberList.get(position);

        holder.tvMemberName.setText(member.getName());
        holder.tvMemberPhone.setText("Phone: " + member.getPhone());

        holder.imgEdit.setOnClickListener(v -> listener.onEditClick(member));
        holder.imgDelete.setOnClickListener(v -> listener.onDeleteClick(member, position));
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvMemberPhone;
        ImageView imgEdit, imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberPhone = itemView.findViewById(R.id.tvMemberPhone);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    public void removeMember(int position) {
        if (position >= 0 && position < memberList.size()) {
            memberList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, memberList.size());
        }
    }
}