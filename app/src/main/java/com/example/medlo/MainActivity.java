package com.example.medlo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav, R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new homeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        replaceFragment(new homeFragment());

        bottomNavigationView.setBackground(null);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//
//            switch (item.getItemId()) {
//                case R.id.home:
//                    replaceFragment(new homeFragment());
//                    break;
//            }
//
//            return true;
//        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            TextView headname= findViewById(R.id.head_name);
            TextView headmail=findViewById(R.id.head_mail);
            // Load user info
            SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
            String userEmail = sp.getString("user_email", null);

            if (userEmail != null) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);

                User user = dbHelper.getUserByEmail(userEmail);
                if (user != null) {
                    headname.setText("Name: " + user.getName());
                    headmail.setText("Email: " + user.getEmail());

                }


            }

            if (id == R.id.nav_home) {
                replaceFragment(new homeFragment());
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_darkmode) {
                ThemeManager.cycleTheme(this);
                recreate(); // recreate activity to apply theme
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_about) {
                Toast.makeText(this, "About Us clicked", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                replaceFragment(new AboutUsFragment());
            } else if (id == R.id.nav_logout) {
                Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this,login.class));
                finish();
            }

            return true;
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                replaceFragment(new AlarmFragment());
                Toast.makeText(MainActivity.this,"New Reminder",Toast.LENGTH_SHORT).show();

            }
        });





        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}