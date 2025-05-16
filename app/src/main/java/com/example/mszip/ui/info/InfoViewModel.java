package com.example.mszip.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mszip.model.service.Service;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InfoViewModel extends ViewModel {
    private final MutableLiveData<List<Service>> services = new MutableLiveData<>();

    public InfoViewModel() {
        loadServices();
    }

    private void loadServices() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Services")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Service service = doc.toObject(Service.class);
                        list.add(service);
                    }
                    services.setValue(list);
                })
                .addOnFailureListener(e -> {
                    //TODO
                });
    }

    public LiveData<List<Service>> getServices() {
        return services;
    }
}
