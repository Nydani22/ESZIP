package com.example.mszip.ui.login;
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
import com.example.mszip.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel =new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth=FirebaseAuth.getInstance();

        if (binding.textView!=null) { //landscape crash
            final TextView textView = binding.textView;

            loginViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        }

        binding.login.setOnClickListener(this::login);

        binding.button2.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.nav_register);
        });
        return root;
    }

    private void login(View view) {
        EditText emailET = binding.editTextEmailAddress.getEditText();
        EditText pwET = binding.editTextPassword.getEditText();

        if (emailET==null && pwET==null) {
            return;
        }
        String email = emailET.getText().toString();
        String pw = pwET.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).refreshMenu();
                }
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.nav_info);
            } else {
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
