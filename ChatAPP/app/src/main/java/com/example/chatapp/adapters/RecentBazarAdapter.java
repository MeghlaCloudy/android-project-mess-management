package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.entity.Bazar;

import java.util.ArrayList;
import java.util.List;

public class RecentBazarAdapter extends RecyclerView.Adapter<RecentBazarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Bazar> bazarList;

    public RecentBazarAdapter(Context context, ArrayList<Bazar> bazarList) {
        this.context = context;
        this.bazarList = bazarList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_bazar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bazar bazar = bazarList.get(position);
        holder.tvItem.setText(bazar.getItemName());
        holder.tvAmount.setText(String.format("%.2f", bazar.getAmount()) + " টাকা");
        holder.tvDate.setText(bazar.getDate());
    }

    @Override
    public int getItemCount() {
        return bazarList.size();
    }

    public void updateData(List<Bazar> newList) {
        bazarList.clear();
        bazarList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem, tvAmount, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvItem);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}