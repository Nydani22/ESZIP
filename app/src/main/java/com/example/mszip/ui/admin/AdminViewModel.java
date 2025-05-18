package com.example.mszip.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<Idopont>> idopontok = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Service>> services = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Idopont>> getIdopontok() { return idopontok; }
    public LiveData<List<Service>> getServices() { return services; }


    public void fetchIdopontok() {
        db.collection("Idopontok")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Idopont> list = new ArrayList<>();
                    for (var doc : qs) {
                        var ip = doc.toObject(Idopont.class);
                        ip.setId(doc.getId());
                        list.add(ip);
                    }
                    idopontok.setValue(list);
                });
    }



    public void deleteIdopont(String idopontId) {
        db.collection("Idopontok").document(idopontId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("Foglalasok")
                            .whereEqualTo("idopontid", idopontId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    doc.getReference().delete();
                                }
                            });
                    fetchIdopontok();
                });
    }



    public void fetchServices() {
        db.collection("Services")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Service> list = new ArrayList<>();
                    for (var doc : qs) {
                        var s = doc.toObject(Service.class);
                        s.setId(doc.getId());
                        list.add(s);
                    }
                    services.setValue(list);
                });
    }

    public void addIdopont(String date, String serviceid) {
        Map<String, Object> data = new HashMap<>();
        data.put("date", date);
        data.put("serviceid", serviceid);
        data.put("available", true);

        db.collection("Idopontok").add(data)
                .addOnSuccessListener(doc -> {
                    String id = doc.getId();
                    doc.update("id", id)
                            .addOnSuccessListener(v -> fetchIdopontok());
                });
    }

    public void addService(String name, int price, String time) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("time", time);
        db.collection("Services").add(data)
                .addOnSuccessListener(doc -> {
                    String id = doc.getId();
                    doc.update("id", id)
                            .addOnSuccessListener(v -> fetchServices());
                });
    }

    public void updateService(String serviceId, String name, int price, String time) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("time", time);
        db.collection("Services").document(serviceId)
                .update(data)
                .addOnSuccessListener(v -> fetchServices());
    }

    public void deleteService(String serviceId) {

        db.collection("Services").document(serviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("Idopontok")
                            .whereEqualTo("serviceid", serviceId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    String idopontId = doc.getId();
                                    deleteIdopont(idopontId);
                                }
                            });

                    fetchServices();
                });
    }

}
