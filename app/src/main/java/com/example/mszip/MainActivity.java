package com.example.mszip;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mszip.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    private String role="";

    private NavController navController;

    private NavigationView navigationView;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_info, R.id.nav_login)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> updateLoginMenuItem(navigationView);
        updateLoginMenuItem(navigationView);

        updateNavHeader();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_logout) {
                logout();
                return true;
            }


            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });
        refreshMenu();
    }

    public void logout() {
        mAuth.signOut();
        navController.navigate(R.id.nav_info);
        updateLoginMenuItem(navigationView);
        role="";
        updateNavHeader();
        refreshMenu();
    }



    public void updateNavHeader() {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.email);
        TextView teljesnevTextView = headerView.findViewById(R.id.teljesnev);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());

            FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            teljesnevTextView.setText(document.getString("teljesnev"));
                            role=document.getString("role");
                            updateLoginMenuItem(navigationView);
                        }
                    });
        } else {
            emailTextView.setText("Nem vagy bejelentkezve");
            teljesnevTextView.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void updateLoginMenuItem(NavigationView navigationView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MenuItem loginItem = navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem profilItem = navigationView.getMenu().findItem(R.id.nav_profile);
        MenuItem adminItem = navigationView.getMenu().findItem(R.id.nav_admin);
        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);
        if (user != null) {
            loginItem.setVisible(false);
            profilItem.setVisible(true);
            logoutItem.setVisible(true);
            adminItem.setVisible(role.equals("admin"));
        } else {
            loginItem.setVisible(true);
            adminItem.setVisible(false);
            profilItem.setVisible(false);
            logoutItem.setVisible(false);
        }
    }

    public void refreshMenu() {
        updateLoginMenuItem(binding.navView);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
