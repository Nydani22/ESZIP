package com.example.mszip.ui.foglal;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mszip.R;
import com.example.mszip.model.service.Service;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FoglalFragment extends Fragment {

    private Spinner monthSpinner, serviceSpinner;
    private RecyclerView dateRecyclerView;
    private final List<String> days = new ArrayList<>();
    private DayAdapter dayAdapter;
    private final List<Service> services = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_foglal, container, false);

        monthSpinner = root.findViewById(R.id.monthSpinner);
        serviceSpinner = root.findViewById(R.id.serviceSpinner);
        dateRecyclerView = root.findViewById(R.id.dateRecyclerView);

        setupMonthSpinner();
        setupServiceSpinner();
        setupRecyclerView();

        return root;
    }

    private void setupMonthSpinner() {
        String[] months = new String[]{
                "Január", "Február", "Március", "Április", "Május", "Június",
                "Július", "Augusztus", "Szeptember", "Október", "November", "December"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, months);
        monthSpinner.setAdapter(adapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDates(position); // hónap index
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupServiceSpinner() {
        // Dummy adatok
        services.add(new Service("id1", "Kijelzőcsere", 25000, "60 perc"));
        services.add(new Service("id2", "Akkucsere", 18000, "45 perc"));

        List<String> serviceNames = new ArrayList<>();
        for (Service s : services) {
            serviceNames.add(s.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, serviceNames);
        serviceSpinner.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        dayAdapter = new DayAdapter(days);
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dateRecyclerView.setAdapter(dayAdapter);
    }

    private void updateDates(int monthIndex) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year, monthIndex, 1); // hónap 0-indexelt

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        days.clear();

        for (int i = 1; i <= maxDay; i++) {
            days.add(String.format("%04d-%02d-%02d", year, monthIndex + 1, i));
        }

        dayAdapter.notifyDataSetChanged();
    }
}
