package com.example.chatapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.MealAdapter;
import com.example.chatapp.db.DatabaseHelper;
import com.example.chatapp.entity.Meal;
import com.example.chatapp.entity.Member;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MealEntryFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private ArrayList<Member> memberList;
    private ArrayList<Meal> mealList;
    private DatePicker datePicker;
    private Spinner spinnerMember;
    private CheckBox chkBreakfast, chkLunch, chkDinner;
    private Button btnSaveMeal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        datePicker = view.findViewById(R.id.datePicker);
        spinnerMember = view.findViewById(R.id.spinnerMember);
        chkBreakfast = view.findViewById(R.id.chkBreakfast);
        chkLunch = view.findViewById(R.id.chkLunch);
        chkDinner = view.findViewById(R.id.chkDinner);
        btnSaveMeal = view.findViewById(R.id.btnSaveMeal);
        recyclerView = view.findViewById(R.id.recyclerViewMeals);

        memberList = dbHelper.getAllMembers();
        mealList = new ArrayList<>();

        // Spinner setup
        ArrayAdapter<Member> memberAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, memberList);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMember.setAdapter(memberAdapter);

        // RecyclerView setup
        adapter = new MealAdapter(requireContext(), mealList, new MealAdapter.MealClickListener() {
            @Override
            public void onDeleteClick(Meal meal, int position) {
                boolean deleted = dbHelper.deleteMeal(meal.getId());
                if (deleted) {
                    adapter.removeMeal(position);
                    Toast.makeText(requireContext(), "মিল ডিলিট হয়েছে", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "ডিলিটে সমস্যা", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Date change listener
        datePicker.init(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                (view1, year, month, day) -> loadMealsForDate(getSelectedDate()));

        // Save button
        btnSaveMeal.setOnClickListener(v -> saveMeal());

        // Initial load
        loadMealsForDate(getSelectedDate());
    }

    private String getSelectedDate() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();
        return String.format(Locale.US, "%d-%02d-%02d", year, month, day);
    }

    private void loadMealsForDate(String date) {
        mealList.clear();
        List<Meal> meals = dbHelper.getMealsByDate(date);
        mealList.addAll(meals);
        adapter.notifyDataSetChanged();

        if (mealList.isEmpty()) {
            Toast.makeText(requireContext(), "এই তারিখে কোনো মিল নেই", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMeal() {
        if (spinnerMember.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "মেম্বার সিলেক্ট করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        Member selectedMember = (Member) spinnerMember.getSelectedItem();
        String date = getSelectedDate();

        int breakfast = chkBreakfast.isChecked() ? 1 : 0;
        int lunch = chkLunch.isChecked() ? 1 : 0;
        int dinner = chkDinner.isChecked() ? 1 : 0;

        if (breakfast == 0 && lunch == 0 && dinner == 0) {
            Toast.makeText(requireContext(), "কমপক্ষে একটা মিল সিলেক্ট করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        Meal meal = new Meal(0, selectedMember.getId(), date, breakfast, lunch, dinner);
        long id = dbHelper.addMeal(meal);

        if (id > 0) {
            Toast.makeText(requireContext(), "মিল সেভ হয়েছে", Toast.LENGTH_SHORT).show();
            chkBreakfast.setChecked(false);
            chkLunch.setChecked(false);
            chkDinner.setChecked(false);
            loadMealsForDate(date);
        } else {
            Toast.makeText(requireContext(), "সেভ হয়নি", Toast.LENGTH_SHORT).show();
        }
        btnSaveMeal.setOnClickListener(v -> {
            Fragment mealListFragment = new MealListFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mealListFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

}