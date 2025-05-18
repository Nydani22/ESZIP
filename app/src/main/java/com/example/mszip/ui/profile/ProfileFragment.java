package com.example.mszip.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mszip.MainActivity;
import com.example.mszip.R;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private EditText emailEditText, teljesnevEditText, jelszoEditText;
    private Button mentButton, torlesButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emailEditText = view.findViewById(R.id.inputEmail);
        teljesnevEditText = view.findViewById(R.id.inputTeljesNev);
        jelszoEditText = view.findViewById(R.id.inputJelszo);
        mentButton = view.findViewById(R.id.buttonMentes);
        torlesButton = view.findViewById(R.id.buttonTorles);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> emailEditText.setText(email));
        viewModel.getTeljesnev().observe(getViewLifecycleOwner(), nev -> teljesnevEditText.setText(nev));
        viewModel.getHibaUzenet().observe(getViewLifecycleOwner(), msg ->
                new AlertDialog.Builder(requireContext()).setTitle("Hiba").setMessage(msg).setPositiveButton("OK",null).show()
        );
        viewModel.getSikeresMentes().observe(getViewLifecycleOwner(), siker -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).updateNavHeader();
            }
            if (siker)
                new AlertDialog.Builder(requireContext()).setTitle("Sikeres mentés!").setPositiveButton("OK",null).show();
        });

        RecyclerView rv = view.findViewById(R.id.recyclerViewFoglalasok);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        FoglalasAdapter adapter = new FoglalasAdapter(new ArrayList<>(), foglalasId -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Foglalás törlése")
                    .setMessage("Biztosan törölni szeretnéd a foglalást?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        viewModel.torolFoglalas(foglalasId);
                    })
                    .setNegativeButton("Mégse", null)
                    .show();

        });
        rv.setAdapter(adapter);

        viewModel.getFoglalasok().observe(getViewLifecycleOwner(), vmList -> {
            adapter.updateList(vmList);
        });
        viewModel.betoltFoglalasok();

        viewModel.betoltAdatok();

        mentButton.setOnClickListener(v -> {
            String nev = teljesnevEditText.getText().toString();
            String jelszo = jelszoEditText.getText().toString();
            if (jelszo.isEmpty()) {
                new AlertDialog.Builder(requireContext()).setTitle("Add meg a jelszót!").setPositiveButton("OK",null).show();
            } else {
                viewModel.mentTeljesnev(nev, jelszo);
            }
        });



        torlesButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Fiók törlése")
                    .setMessage("Biztosan törölni szeretnéd a fiókodat? Ez a művelet nem visszavonható.")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        viewModel.torolFelh();
                        ((MainActivity) requireActivity()).logout();
                    })
                    .setNegativeButton("Mégse", null)
                    .show();
        });
    }
}
