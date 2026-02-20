package com.example.chatapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.db.DatabaseHelper;
import com.example.chatapp.entity.Member;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthlyBillFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private Spinner spinnerMonthYear;
    private Button btnCalculate, btnExportPdf, btnShare;
    private TextView tvTotalMeal, tvTotalBazar, tvPerMealCost, tvBillDetails;

    private String currentBillText = ""; // PDF আর শেয়ারের জন্য টেক্সট সেভ করবো

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_bill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        spinnerMonthYear = view.findViewById(R.id.spinnerMonthYear);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        btnExportPdf = view.findViewById(R.id.btnExportPdf);
        btnShare = view.findViewById(R.id.btnShare);
        tvTotalMeal = view.findViewById(R.id.tvTotalMeal);
        tvTotalBazar = view.findViewById(R.id.tvTotalBazar);
        tvPerMealCost = view.findViewById(R.id.tvPerMealCost);
        tvBillDetails = view.findViewById(R.id.tvBillDetails);

        // মাস-বছর লোড করা (গত ১২ মাস)
        List<String> monthYears = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);

        for (int i = 0; i < 12; i++) {
            monthYears.add(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, monthYears);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonthYear.setAdapter(adapter);

        spinnerMonthYear.setSelection(0);

        btnCalculate.setOnClickListener(v -> calculateBill());

        btnExportPdf.setOnClickListener(v -> exportToPdf());

        btnShare.setOnClickListener(v -> shareBill());
    }

    private void calculateBill() {
        String selected = spinnerMonthYear.getSelectedItem().toString();
        String[] parts = selected.split(" ");
        String month = parts[0];
        int year = Integer.parseInt(parts[1]);

        Calendar startCal = Calendar.getInstance();
        startCal.set(year, getMonthNumber(month), 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startCal.getTime());

        startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startCal.getTime());

        int totalMeal = dbHelper.getTotalMealBetween(startDate, endDate);
        double totalBazar = dbHelper.getTotalBazarBetween(startDate, endDate);

        if (totalMeal == 0) {
            Toast.makeText(requireContext(), "এই মাসে কোনো মিল নেই", Toast.LENGTH_LONG).show();
            resetViews();
            return;
        }

        double perMealCost = totalBazar / totalMeal;

        StringBuilder details = new StringBuilder("প্রত্যেক মেম্বারের বিল (" + selected + "):\n\n");
        List<Member> members = dbHelper.getAllMembers();
        for (Member member : members) {
            int memberMeal = dbHelper.getTotalMealByMember(member.getId(), startDate, endDate);
            double bill = memberMeal * perMealCost;
            details.append(member.getName())
                    .append(": ")
                    .append(String.format("%.2f", bill))
                    .append(" টাকা (")
                    .append(memberMeal)
                    .append(" মিল)\n");
        }

        currentBillText = details.toString(); // PDF/শেয়ারের জন্য সেভ

        tvTotalMeal.setText("মোট মিল: " + totalMeal);
        tvTotalBazar.setText("মোট খরচ: " + String.format("%.2f", totalBazar) + " টাকা");
        tvPerMealCost.setText("প্রতি মিলের দাম: " + String.format("%.2f", perMealCost) + " টাকা");
        tvBillDetails.setText(currentBillText);
    }

    private void resetViews() {
        tvTotalMeal.setText("মোট মিল: ০");
        tvTotalBazar.setText("মোট খরচ: ০.০০ টাকা");
        tvPerMealCost.setText("প্রতি মিলের দাম: ০.০০ টাকা");
        tvBillDetails.setText("কোনো বিল নেই");
        currentBillText = "";
    }

    private void exportToPdf() {
        if (currentBillText.isEmpty()) {
            Toast.makeText(requireContext(), "প্রথমে বিল হিসাব করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Android 10+ এর জন্য সেফ জায়গা (অ্যাপের নিজস্ব স্টোরেজ)
            File pdfDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "MessBills");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            File file = new File(pdfDir, "Mess_Bill_" + System.currentTimeMillis() + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.add(new Paragraph("মাসিক মেস বিল"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(currentBillText));

            document.close();

            Toast.makeText(requireContext(), "PDF সেভ হয়েছে: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // অপশনাল: PDF ওপেন করার চেষ্টা
            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(requireContext(), "PDF তৈরিতে সমস্যা: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareBill() {
        if (currentBillText.isEmpty()) {
            Toast.makeText(requireContext(), "প্রথমে বিল হিসাব করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentBillText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "মাসিক মেস বিল");
        startActivity(Intent.createChooser(shareIntent, "বিল শেয়ার করুন"));
    }

    private int getMonthNumber(String monthName) {
        switch (monthName) {
            case "January": return 0;
            case "February": return 1;
            case "March": return 2;
            case "April": return 3;
            case "May": return 4;
            case "June": return 5;
            case "July": return 6;
            case "August": return 7;
            case "September": return 8;
            case "October": return 9;
            case "November": return 10;
            case "December": return 11;
            default: return 0;
        }
    }
}