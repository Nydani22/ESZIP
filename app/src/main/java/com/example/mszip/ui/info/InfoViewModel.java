package com.example.mszip.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mszip.model.service.Service;

import java.util.ArrayList;
import java.util.List;

public class InfoViewModel extends ViewModel {
    private final MutableLiveData<List<Service>> services = new MutableLiveData<>();

    public InfoViewModel() {
        loadServices();
    }

    private void loadServices() {
        List<Service> list = new ArrayList<>();
        list.add(new Service("Kijelzőcsere (okostelefon)", "25.000 Ft", "kb. 60 perc"));
        list.add(new Service("Akkumulátorcsere (laptop)", "18.000 Ft", "kb. 45 perc"));
        list.add(new Service("Alaplapi hiba javítása", "30.000 Ft-tól", "1-3 munkanap"));
        list.add(new Service("USB-port javítás", "10.000 Ft", "kb. 40 perc"));
        list.add(new Service("Adatmentés sérült HDD-ről", "20.000 Ft-tól", "1-2 munkanap"));
        list.add(new Service("Vízkár diagnosztika és tisztítás", "15.000 Ft", "kb. 1 óra"));
        list.add(new Service("Szoftveres helyreállítás (boot hiba)", "8.000 Ft", "kb. 30 perc"));
        services.setValue(list);
    }

    public LiveData<List<Service>> getServices() {
        return services;
    }
}
