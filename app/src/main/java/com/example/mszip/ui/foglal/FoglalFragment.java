package com.example.mszip.ui.foglal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mszip.databinding.FragmentFoglalBinding;

public class FoglalFragment extends Fragment {
    private FragmentFoglalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FoglalViewModel foglalViewModel =
                new ViewModelProvider(this).get(FoglalViewModel.class);

        binding = FragmentFoglalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFoglal;
        foglalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
