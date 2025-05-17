package com.example.mszip.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mszip.MainActivity;
import com.example.mszip.R;
import com.example.mszip.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RegisterViewModel registerViewModel =
                new ViewModelProvider(this).get(RegisterViewModel.class);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        final TextView textView = binding.textView;
        registerViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.button2.setOnClickListener(this::registerUser);

        return root;
    }

    private void registerUser(View view) {
        EditText teljesnevET = binding.editTextNev.getEditText();
        EditText emailET = binding.editTextEmailAddress.getEditText();
        EditText pwET = binding.editTextPassword.getEditText();
        EditText pwAgainET = binding.editTextPasswordagain.getEditText();

        if (teljesnevET == null || emailET == null || pwET == null || pwAgainET == null) {
            Toast.makeText(getContext(), "Hiba történt a mezők elérésében", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = teljesnevET.getText().toString();
        String email = emailET.getText().toString();
        String pw = pwET.getText().toString();
        String pw2 = pwAgainET.getText().toString();

        if (!pw.equals(pw2)) {
            Toast.makeText(getContext(), "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> user = new HashMap<>();
                user.put("id", uid);
                user.put("teljesnev", name);
                user.put("email", email);
                user.put("role", "user");

                db.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).refreshMenu();
                            }
                            Navigation.findNavController(requireView()).navigate(R.id.nav_info);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Hiba a Firestore mentéskor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            } else {
                Toast.makeText(getContext(), "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
