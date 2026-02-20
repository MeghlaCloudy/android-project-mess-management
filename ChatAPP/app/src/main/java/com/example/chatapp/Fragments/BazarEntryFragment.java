package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.BazarAdapter;
import com.example.chatapp.db.DatabaseHelper;
import com.example.chatapp.entity.Bazar;
import com.example.chatapp.entity.Member;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BazarEntryFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private BazarAdapter adapter;
    private ArrayList<Bazar> bazarList = new ArrayList<>(); // Initialized here

    private DatePicker datePicker;
    private EditText etItemName, etAmount;
    private Spinner spinnerPaidBy;
    private Button btnSaveBazar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bazar_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        datePicker = view.findViewById(R.id.datePickerBazar);
        etItemName = view.findViewById(R.id.etItemName);
        etAmount = view.findViewById(R.id.etAmount);
        spinnerPaidBy = view.findViewById(R.id.spinnerPaidBy);
        btnSaveBazar = view.findViewById(R.id.btnSaveBazar);
        recyclerView = view.findViewById(R.id.recyclerViewBazar);

        // Setup Spinner with members
        List<Member> memberList = dbHelper.getAllMembers();
        if (memberList.isEmpty()) {
            Toast.makeText(requireContext(), "No members found. Please add members first.", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<Member> memberAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                memberList);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaidBy.setAdapter(memberAdapter);

        // Setup RecyclerView
        adapter = new BazarAdapter(requireContext(), bazarList, new BazarAdapter.BazarClickListener() {
            @Override
            public void onDeleteClick(Bazar bazar, int position) {
                boolean deleted = dbHelper.deleteBazar(bazar.getId());
                if (deleted) {
                    bazarList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, bazarList.size());
                    Toast.makeText(requireContext(), "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to delete expense", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // DatePicker listener
        datePicker.init(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                (view1, year, month, day) -> loadBazarForDate(getSelectedDate())
        );

        // Save button
        btnSaveBazar.setOnClickListener(v -> saveBazar());

        // Initial load
        loadBazarForDate(getSelectedDate());
    }

    private String getSelectedDate() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1; // Month is 0-based
        int day = datePicker.getDayOfMonth();
        return String.format(Locale.US, "%d-%02d-%02d", year, month, day);
    }

    private void loadBazarForDate(String date) {
        bazarList.clear();
        List<Bazar> bazars = dbHelper.getBazarByDate(date);
        bazarList.addAll(bazars);
        adapter.notifyDataSetChanged();

        if (bazarList.isEmpty()) {
            Toast.makeText(requireContext(), "No expenses found on this date", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBazar() {
        String itemName = etItemName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (itemName.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter item name and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        Member paidBy = (Member) spinnerPaidBy.getSelectedItem();
        if (paidBy == null) {
            Toast.makeText(requireContext(), "Please select who paid", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = getSelectedDate();

        Bazar bazar = new Bazar(0, date, itemName, amount, paidBy.getId());
        long id = dbHelper.addBazar(bazar);

        if (id > 0) {
            Toast.makeText(requireContext(), "Expense saved successfully", Toast.LENGTH_SHORT).show();
            etItemName.setText("");
            etAmount.setText("");
            loadBazarForDate(date);
        } else {
            Toast.makeText(requireContext(), "Failed to save expense", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBazarForDate(getSelectedDate()); // Refresh when fragment resumes
    }
}