package com.example.mszip.ui.login;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mszip.MainActivity;
import com.example.mszip.R;
import com.example.mszip.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogle;

    private ActivityResultLauncher<Intent> googleSignInLauncher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Toast.makeText(getContext(), "Google bejelentkezés sikertelen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        if (binding.textView!=null) {
            final TextView textView = binding.textView;
            loginViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        }

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogle=GoogleSignIn.getClient(requireContext(), gso);

        binding.login.setOnClickListener(this::login);

        binding.button.setOnClickListener(this::loginWithGoogle);

        binding.button2.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.nav_register);
        });

        return root;
    }



    private void loginWithGoogle(View view) {
        Intent signInIntent = mGoogle.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    DocumentReference userRef = db.collection("Users").document(uid);

                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("id", uid);
                            newUser.put("teljesnev", firebaseUser.getDisplayName());
                            newUser.put("email", firebaseUser.getEmail());
                            newUser.put("role", "user");

                            userRef.set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        if (getActivity() instanceof MainActivity) {
                                            ((MainActivity) requireActivity()).updateNavHeader();
                                            ((MainActivity) getActivity()).refreshMenu();
                                        }
                                        Navigation.findNavController(requireView()).navigate(R.id.nav_info);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Hiba a mentésnél: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) requireActivity()).updateNavHeader();
                                ((MainActivity) getActivity()).refreshMenu();
                            }
                            Navigation.findNavController(requireView()).navigate(R.id.nav_info);
                        }
                    });
                }

            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Google belépés sikertelen!").setPositiveButton("OK",null).show();
            }
        });
    }



    private void login(View view) {
        EditText emailET = binding.editTextEmailAddress.getEditText();
        EditText pwET = binding.editTextPassword.getEditText();

        if (emailET==null || pwET == null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sikertelen bejelentkezés!")
                    .setMessage("Nem adtad meg az emailt vagy a jelszót.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        String email = emailET.getText().toString();
        String pw = pwET.getText().toString();

        if (email.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sikertelen bejelentkezés!")
                    .setMessage("Nem adtad meg az emailt.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        } else if (pw.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sikertelen bejelentkezés!")
                    .setMessage("Nem adtad meg a jelszót.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).refreshMenu();
                    ((MainActivity) requireActivity()).updateNavHeader();
                }
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.nav_info);
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Sikertelen bejelentkezés!").setPositiveButton("OK",null).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
