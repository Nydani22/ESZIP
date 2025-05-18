package com.example.mszip.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mszip.R;
import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


public class AdminFragment extends Fragment {
    private AdminViewModel vm;
    private RecyclerView rvIdopontok, rvServices;
    private AdminAdapter<Idopont> idopontAdapter;
    private AdminAdapter<Service> serviceAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(AdminViewModel.class);

        rvIdopontok = view.findViewById(R.id.recyclerViewIdopontok);
        rvServices = view.findViewById(R.id.recyclerViewServices);


        idopontAdapter = new AdminAdapter<>(List.of(), "Időpont: %s", item -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Törlés megerősítése")
                    .setMessage("Biztos törlöd az időpontot?" )
                    .setPositiveButton("Igen", (d,w) -> vm.deleteIdopont(item.getId()))
                    .setNegativeButton("Mégse", null)
                    .show();
        });
        rvIdopontok.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIdopontok.setAdapter(idopontAdapter);

        view.findViewById(R.id.buttonAddIdopont).setOnClickListener(v -> showAddIdopontDialog());
        vm.getIdopontok().observe(getViewLifecycleOwner(), list -> idopontAdapter.updateList(list));
        vm.fetchIdopontok();

        serviceAdapter = new AdminAdapter<>(
                List.of(),
                "Szolgáltatás: %s",
                item -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Törlés megerősítése")
                            .setMessage("Biztos törlöd a szolgáltatást?" )
                            .setPositiveButton("Igen", (d,w) -> vm.deleteService(item.getId()))
                            .setNegativeButton("Mégse", null)
                            .show();
                },
                this::showEditServiceDialog
        );
        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvServices.setAdapter(serviceAdapter);

        view.findViewById(R.id.buttonAddService).setOnClickListener(v -> showAddServiceDialog());
        vm.getServices().observe(getViewLifecycleOwner(), list -> serviceAdapter.updateList(list));
        vm.fetchServices();
    }

    private void showAddIdopontDialog() {
        View dlg = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_idopont, null);
        EditText etDate = dlg.findViewById(R.id.editDatetext);
        Spinner spService = dlg.findViewById(R.id.spinnerService);


        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        dlg.startAnimation(anim);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                vm.getServices().getValue().stream().map(Service::getName).collect(Collectors.toList()));
        spService.setAdapter(adapter);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Új időpont")
                .setView(dlg)
                .setPositiveButton("Mentés", null)
                .setNegativeButton("Mégse", null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String dateStr = etDate.getText().toString();

                if (!isValidDate(dateStr)) {
                    etDate.setError("Érvénytelen dátum! Használd a yyyy-MM-dd formátumot.");
                    return;
                }

                if (isDateBeforeToday(dateStr)) {
                    etDate.setError("A dátum nem lehet a mai napnál régebbi!");
                    return;
                }

                String serviceId = vm.getServices().getValue().get(spService.getSelectedItemPosition()).getId();
                vm.addIdopont(dateStr, serviceId);
                alertDialog.dismiss();
            });
        });

        alertDialog.show();
    }
    private boolean isValidDate(String dateStr) {
        if (dateStr == null || !dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (month < 1 || month > 12 || day < 1 || day > 31) {
                return false;
            }

            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day);
            cal.getTime();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDateBeforeToday(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar inputDate = Calendar.getInstance();
            inputDate.set(year, month - 1, day, 0, 0, 0);
            inputDate.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return inputDate.before(today);

        } catch (Exception e) {
            return true;
        }
    }





    private void showEditServiceDialog(Service service) {
        View dlg = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_service, null);
        EditText etName = dlg.findViewById(R.id.editName);
        EditText etPrice = dlg.findViewById(R.id.editPrice);
        EditText etTime = dlg.findViewById(R.id.editTime);

        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        dlg.startAnimation(anim);

        etName.setText(service.getName());
        etPrice.setText(String.valueOf(service.getPrice()));
        etTime.setText(service.getTime());

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Szolgáltatás szerkesztése")
                .setView(dlg)
                .setPositiveButton("Mentés", null)
                .setNegativeButton("Mégse", null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String time = etTime.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError("Add meg a nevet.");
                    return;
                }

                if (priceStr.isEmpty()) {
                    etPrice.setError("Add meg az árat.");
                    return;
                }

                int price;
                try {
                    price = Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    etPrice.setError("Az árnak számnak kell lennie.");
                    return;
                }

                if (time.isEmpty()) {
                    etTime.setError("Add meg az intervallumot.");
                    return;
                }

                vm.updateService(service.getId(), name, price, time);
                alertDialog.dismiss();
            });
        });
        alertDialog.show();
    }


    private void showAddServiceDialog() {
        View dlg = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_service, null);
        EditText etName = dlg.findViewById(R.id.editName);
        EditText etPrice = dlg.findViewById(R.id.editPrice);
        EditText etTime = dlg.findViewById(R.id.editTime);

        Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        dlg.startAnimation(anim);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Új szolgáltatás")
                .setView(dlg)
                .setPositiveButton("Mentés", null)
                .setNegativeButton("Mégse", null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String time = etTime.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError("Add meg a nevet.");
                    return;
                }

                if (priceStr.isEmpty()) {
                    etPrice.setError("Add meg az árat.");
                    return;
                }

                int price;
                try {
                    price = Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    etPrice.setError("Az árnak számnak kell lennie.");
                    return;
                }

                if (time.isEmpty()) {
                    etTime.setError("Add meg az intervallumot.");
                    return;
                }

                vm.addService(name, price, time);
                alertDialog.dismiss();
            });
        });
        alertDialog.show();
    }
}
