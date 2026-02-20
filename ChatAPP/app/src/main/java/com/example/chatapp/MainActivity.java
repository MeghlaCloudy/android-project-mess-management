package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.chatapp.Fragments.AddMemberBottomSheet;
import com.example.chatapp.Fragments.BazarEntryFragment;
import com.example.chatapp.Fragments.ChatFragment;
import com.example.chatapp.Fragments.HomeFragment;
import com.example.chatapp.Fragments.MealEntryFragment;
import com.example.chatapp.Fragments.MembersFragment;
import com.example.chatapp.Fragments.MonthlyBillFragment;
import com.example.chatapp.Fragments.NoteFragment;
import com.example.chatapp.Fragments.NoteViewFragment;
import com.example.chatapp.Fragments.ProfileFragment;
import com.example.chatapp.ui.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // প্রথমবার বা rotation-এ duplicate এড়ানো
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // তোমার আইকন
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        String title = "Mess Management";

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            title = "Home";
        } else if (id == R.id.nav_note) {
            fragment = new NoteFragment();
            title = "Chat";
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            title = "Profile";
        }

     else if (id == R.id.nav_members) {
        fragment = new MembersFragment();
        title = "Members";
    } else if (id == R.id.nav_meal_entry) {
        fragment = new MealEntryFragment();
        title = "Meals";
    }    else if (id == R.id.nav_bazar) {
            fragment = new BazarEntryFragment();
            title = "Bazar";
        } else if (id == R.id.nav_monthly_bill) {
            fragment = new MonthlyBillFragment();
            title = "Bill";
        }else if (id == R.id.nav_view_note) {
            fragment = new NoteViewFragment();
        }

        else if (id == R.id.nav_logout) {
            // লগআউট লজিক (উদাহরণস্বরূপ)
            // FirebaseAuth.getInstance().signOut();  // যদি firebase থাকে
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if (fragment != null) {
            loadFragment(fragment);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)   // ← এই ID ঠিক করো
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
}