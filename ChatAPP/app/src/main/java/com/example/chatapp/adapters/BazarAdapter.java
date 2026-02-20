package com.example.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.entity.Bazar;
import com.example.chatapp.entity.Member;

import java.util.ArrayList;

public class BazarAdapter extends RecyclerView.Adapter<BazarAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Bazar> bazarList;
    private BazarClickListener listener;

    public interface BazarClickListener {
        void onDeleteClick(Bazar bazar, int position);
    }

    public BazarAdapter(Context context, ArrayList<Bazar> bazarList, BazarClickListener listener) {
        this.context = context;
        this.bazarList = bazarList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bazar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bazar bazar = bazarList.get(position);

        holder.tvItemName.setText(bazar.getItemName());
        holder.tvAmount.setText("টাকা: " + String.format("%.2f", bazar.getAmount()));
        holder.tvDate.setText("তারিখ: " + bazar.getDate());

        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(bazar, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bazarList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvAmount, tvDate;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    public void removeBazar(int position) {
        bazarList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, bazarList.size());
    }
}