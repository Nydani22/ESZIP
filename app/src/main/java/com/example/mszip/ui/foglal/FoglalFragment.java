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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mszip.R;
import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoglalFragment extends Fragment implements DayAdapter.OnDayClickListener {

    private Spinner monthSpinner, serviceSpinner;
    private RecyclerView dateRecyclerView;
    private Button btnFoglal;

    private final List<Service> services = new ArrayList<>();
    private final List<String> days = new ArrayList<>();
    private DayAdapter dayAdapter;

    private FirebaseFirestore db;

    private final List<Idopont> idopontok = new ArrayList<>();
    private final Set<String> foglaltNapok = new HashSet<>();

    private String selectedDate = null;
    private Service selectedService = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_foglal, container, false);

        monthSpinner = root.findViewById(R.id.monthSpinner);
        serviceSpinner = root.findViewById(R.id.serviceSpinner);
        dateRecyclerView = root.findViewById(R.id.dateRecyclerView);
        btnFoglal = root.findViewById(R.id.btnFoglal);

        db = FirebaseFirestore.getInstance();

        setupMonthSpinner();
        setupServiceSpinner();

        dayAdapter = new DayAdapter(days, foglaltNapok, this);
        dateRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7)); // hét napos rács
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
                Toast.makeText(requireContext(), "Kérlek válassz egy napot!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedService == null) {
                Toast.makeText(requireContext(), "Kérlek válassz egy szolgáltatást!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Itt jönne a foglalás logika, most csak egy üzenet
            new AlertDialog.Builder(requireContext())
                    .setTitle("Foglalás")
                    .setMessage("Foglalás: " + selectedService.name + "\nDátum: " + selectedDate)
                    .setPositiveButton("OK", null)
                    .show();
        });

        return root;
    }

    private void setupMonthSpinner() {
        String[] months = new String[]{
                "Január", "Február", "Március", "Április", "Május", "Június",
                "Július", "Augusztus", "Szeptember", "Október", "November", "December"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, months);
        monthSpinner.setAdapter(adapter);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonth);
    }

    private void setupServiceSpinner() {
        services.clear();
        services.add(new Service("s1", "Kijelzőcsere", 25000, "60 perc"));
        services.add(new Service("s2", "Akkucsere", 18000, "45 perc"));
        services.add(new Service("s3", "Alaplap javítás", 30000, "2 nap"));

        List<String> serviceNames = new ArrayList<>();
        for (Service s : services) {
            serviceNames.add(s.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, serviceNames);
        serviceSpinner.setAdapter(adapter);

        if (!services.isEmpty()) {
            selectedService = services.get(0);
        }
    }

    private void updateDays() {
        days.clear();
        foglaltNapok.clear();
        selectedDate = null;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = monthSpinner.getSelectedItemPosition(); // 0-based hónap

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDay; i++) {
            days.add(String.format("%04d-%02d-%02d", year, month + 1, i));
        }
        dayAdapter.notifyDataSetChanged();
    }

    private void loadIdopontokForSelectedService() {
        if (selectedService == null) return;

        db.collection("idopontok")
                .whereEqualTo("serviceid", selectedService.id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        idopontok.clear();
                        foglaltNapok.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String date = doc.getString("date");
                            Boolean available = doc.getBoolean("available");
                            if (date != null && available != null && !available) {
                                foglaltNapok.add(date);
                            }
                        }
                        dayAdapter.setFoglaltNapok(foglaltNapok);
                        dayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Nem sikerült betölteni az időpontokat", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDayClick(String day) {
        if (!foglaltNapok.contains(day)) {
            selectedDate = day;
            dayAdapter.setSelectedDate(day);
            dayAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(requireContext(), "Ez a nap már foglalt", Toast.LENGTH_SHORT).show();
        }
    }
}
