package com.example.mszip;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_CODE = 100;

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_HEADER_IMAGE_URI = "header_image_uri";


    private ImageView headerImageView;


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
        View headerView = binding.navView.getHeaderView(0);
        headerImageView = headerView.findViewById(R.id.imageView);


        headerImageView.setOnClickListener(v -> {
            if (checkPermissions()) {
                openImageChooser();
            }
        });
        loadSavedImageUri();
        refreshMenu();
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_CODE
                );
                return false;
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_CODE
                );
                return false;
            }
        }
        return true;
    }



    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void loadSavedImageUri() {
        String uriString = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_HEADER_IMAGE_URI, null);
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            headerImageView.setImageURI(uri);
        }
    }



    private void saveImageUri(Uri uri) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_HEADER_IMAGE_URI, uri.toString())
                .apply();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this,
                        "Engedély szükséges a képek eléréséhez",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            Uri imageUri = data.getData();
            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            headerImageView.setImageURI(imageUri);
            saveImageUri(imageUri);
        }
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
