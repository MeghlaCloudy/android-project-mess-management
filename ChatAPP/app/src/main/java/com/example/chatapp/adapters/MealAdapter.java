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
import com.example.chatapp.db.DatabaseHelper;
import com.example.chatapp.entity.Meal;
import com.example.chatapp.entity.Member;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Meal> mealList;
    private final DatabaseHelper dbHelper;
    private final MealClickListener listener;

    public interface MealClickListener {
        void onDeleteClick(Meal meal, int position);
    }

    public MealAdapter(Context context, ArrayList<Meal> mealList, MealClickListener listener) {
        this.context = context;
        this.mealList = mealList;
        this.dbHelper = new DatabaseHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = mealList.get(position);

        // মেম্বারের নাম লোড
        Member member = dbHelper.getMemberById(meal.getMemberId());
        holder.tvMemberName.setText(member != null ? member.getName() : "Unknown");

        // মিল স্ট্যাটাস
        String status = "B: " + (meal.getBreakfast() == 1 ? "✔" : "❌") +
                "  L: " + (meal.getLunch() == 1 ? "✔" : "❌") +
                "  D: " + (meal.getDinner() == 1 ? "✔" : "❌");
        holder.tvMealStatus.setText(status);

        // ডিলিট ক্লিক
        holder.imgDeleteMeal.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(meal, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvMealStatus;
        ImageView imgDeleteMeal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMealStatus = itemView.findViewById(R.id.tvMealStatus);
            imgDeleteMeal = itemView.findViewById(R.id.imgDeleteMeal);
        }
    }

    public void removeMeal(int position) {
        if (position >= 0 && position < mealList.size()) {
            mealList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mealList.size());
        }
    }
}