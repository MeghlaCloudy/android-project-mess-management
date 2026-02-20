package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.RecentBazarAdapter;
import com.example.chatapp.db.DatabaseHelper;
import com.example.chatapp.entity.Bazar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvTotalMembers, tvTodayMeal, tvTotalMealMonth, tvTotalExpense, tvPerMealCost;
    private RecyclerView recyclerViewRecentBazar;
    private RecentBazarAdapter recentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        tvTotalMembers = view.findViewById(R.id.tvTotalMembers);
        tvTodayMeal = view.findViewById(R.id.tvTodayMeal);
        tvTotalMealMonth = view.findViewById(R.id.tvTotalMealMonth);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvPerMealCost = view.findViewById(R.id.tvPerMealCost);
        recyclerViewRecentBazar = view.findViewById(R.id.recyclerViewRecentBazar);

        // Recent Bazar setup
        ArrayList<Bazar> recentBazar = new ArrayList<>();
        recentAdapter = new RecentBazarAdapter(requireContext(), recentBazar);
        recyclerViewRecentBazar.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewRecentBazar.setAdapter(recentAdapter);

        loadDashboardData();
    }

    private void loadDashboardData() {
        // মোট মেম্বার
        int totalMembers = dbHelper.getAllMembers().size();
        tvTotalMembers.setText(String.valueOf(totalMembers));

        // আজকের মিল
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
        int todayMeal = dbHelper.getTotalMealByDate(today);
        tvTodayMeal.setText(String.valueOf(todayMeal));

        // এই মাসের সারাংশ
        Calendar cal = Calendar.getInstance();
        String startDate = new SimpleDateFormat("yyyy-MM-01", Locale.US).format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());

        int totalMealMonth = dbHelper.getTotalMealBetween(startDate, endDate);
        double totalExpense = dbHelper.getTotalBazarBetween(startDate, endDate);
        double perMealCost = totalMealMonth > 0 ? totalExpense / totalMealMonth : 0;

        tvTotalMealMonth.setText("মোট মিল: " + totalMealMonth);
        tvTotalExpense.setText("মোট খরচ: " + String.format("%.2f", totalExpense) + " টাকা");
        tvPerMealCost.setText("প্রতি মিলের দাম: " + String.format("%.2f", perMealCost) + " টাকা");

        // সাম্প্রতিক ৫টা বাজার
        List<Bazar> recent = dbHelper.getRecentBazar(5);
        recentAdapter.updateData(recent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData(); // ফিরে এলে আপডেট
    }
}