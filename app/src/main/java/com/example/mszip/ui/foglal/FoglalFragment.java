package com.example.mszip.ui.foglal;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mszip.R;
import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FoglalFragment extends Fragment implements DayAdapter.OnDayClickListener {

    private Spinner monthSpinner, serviceSpinner;
    private RecyclerView dateRecyclerView;
    private Button btnFoglal;

    private final List<Service> services = new ArrayList<>();
    private final List<String> days = new ArrayList<>();
    private DayAdapter dayAdapter;

    private FirebaseFirestore db;
    private Idopont selectedIdopont = null;


    private final List<Idopont> idopontok = new ArrayList<>();
    private final Set<String> foglaltNapok = new HashSet<>();
    private final Map<String, Idopont> napIdopontMap = new HashMap<>();

    private String selectedDate = null;
    private FirebaseUser user;


    private Service selectedService = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_foglal, container, false);
        FoglalViewModel viewModel = new ViewModelProvider(this).get(FoglalViewModel.class);
        monthSpinner = root.findViewById(R.id.monthSpinner);
        serviceSpinner = root.findViewById(R.id.serviceSpinner);
        dateRecyclerView = root.findViewById(R.id.dateRecyclerView);
        btnFoglal = root.findViewById(R.id.btnFoglal);
        String selectedServiceId = getArguments() != null ? getArguments().getString("selectedServiceId") : null;


        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setupMonthSpinner();

        dayAdapter = new DayAdapter(days, foglaltNapok, this);
        dateRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        dateRecyclerView.setAdapter(dayAdapter);

        updateDays();

        monthSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                updateDays();
                loadIdopontokForSelectedService();
            }
        });

        serviceSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                selectedService = services.get(position);
                updateDays();
                loadIdopontokForSelectedService();
            }
        });

        btnFoglal.setOnClickListener(v -> {
            if (selectedDate == null) {
                new AlertDialog.Builder(requireContext()).setTitle("Hiba").setMessage("Kérlek válassz egy napot!").setPositiveButton("OK",null).show();
                return;
            }
            if (selectedService == null) {
                new AlertDialog.Builder(requireContext()).setTitle("Hiba").setMessage("Kérlek válassz egy szolgáltatást!").setPositiveButton("OK",null).show();
                return;
            }

            if (user == null) {
                Toast.makeText(requireContext(), "Nem vagy bejelentkezve", Toast.LENGTH_SHORT).show();
                return;
            }
            String idopontId = selectedIdopont.id;
            Map<String, Object> foglalas = new HashMap<>();
            foglalas.put("userid", user.getUid());
            foglalas.put("idopontid", idopontId);
            FirebaseFirestore.getInstance()
                    .collection("Foglalasok")
                    .add(foglalas)
                    .addOnSuccessListener(documentReference -> {
                        FirebaseFirestore.getInstance()
                                .collection("Idopontok")
                                .document(idopontId)
                                .update("available", false)
                                .addOnSuccessListener(aVoid -> {
                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Sikeres Foglalás!")
                                            .setMessage("Foglalás: " + selectedService.name + "\nDátum: " + selectedDate)
                                            .setPositiveButton("OK", null)
                                            .show();
                                    updateDays();
                                    loadIdopontokForSelectedService();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Időpont frissítés sikertelen: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Sikertelen foglalás: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        });

        viewModel.getServices().observe(getViewLifecycleOwner(), fetchedServices -> {
            services.clear();
            services.addAll(fetchedServices);

            List<String> serviceNames = new ArrayList<>();
            for (Service s : services) {
                serviceNames.add(s.name);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, serviceNames);
            serviceSpinner.setAdapter(adapter);

            if (!services.isEmpty()) {
                selectedService = services.get(0);
                loadIdopontokForSelectedService();
            }

            if (selectedServiceId != null) {
                for (int i = 0; i < services.size(); i++) {
                    if (services.get(i).id.equals(selectedServiceId)) {
                        serviceSpinner.setSelection(i);
                        selectedService = services.get(i);
                        break;
                    }
                }
            }
        });

        viewModel.fetchServices();

        return root;
    }

    private void setupMonthSpinner() {
        String[] allMonths = new String[]{
                "Január", "Február", "Március", "Április", "Május", "Június",
                "Július", "Augusztus", "Szeptember", "Október", "November", "December"
        };

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        List<String> remainingMonths = new ArrayList<>();
        for (int i = currentMonth; i < allMonths.length; i++) {
            remainingMonths.add(allMonths[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, remainingMonths);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setSelection(0);
    }




    private void updateDays() {
        days.clear();
        foglaltNapok.clear();
        selectedDate = null;
        dayAdapter.setSelectedDate(null);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-based
        int selectedIndex = monthSpinner.getSelectedItemPosition();
        int realMonth = currentMonth + selectedIndex;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, realMonth, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDay; i++) {
            days.add(String.format("%04d-%02d-%02d", year, realMonth + 1, i));
        }
        dayAdapter.notifyDataSetChanged();
    }


    private void loadIdopontokForSelectedService() {
        if (selectedService == null) return;

        db.collection("Idopontok")
                .whereEqualTo("serviceid", selectedService.id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        idopontok.clear();
                        foglaltNapok.clear();
                        napIdopontMap.clear();
                        Set<String> elerhetoNapok = new HashSet<>();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String date = doc.getString("date");
                            Boolean available = doc.getBoolean("available");

                            if (date != null) {
                                Idopont idopont = doc.toObject(Idopont.class);
                                elerhetoNapok.add(date);
                                napIdopontMap.put(date, idopont);
                                if (!Boolean.TRUE.equals(available)) {
                                    foglaltNapok.add(date);
                                }
                            }
                        }

                        dayAdapter.setFoglaltNapok(foglaltNapok);
                        dayAdapter.setElerhetoNapok(elerhetoNapok);
                        dayAdapter.setNapIdopontMap(napIdopontMap);
                        dayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Nem sikerült betölteni az időpontokat", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onDayClick(String day, Idopont idopont) {

        if (idopont != null && !foglaltNapok.contains(day)) {
            selectedDate = day;
            selectedIdopont = idopont;
            dayAdapter.setSelectedDate(day);
            dayAdapter.notifyDataSetChanged();
        } else {
            new AlertDialog.Builder(requireContext()).setTitle("Hiba").setMessage("Ez a nap nem elérhető!").setPositiveButton("OK",null).show();
        }
    }
}
