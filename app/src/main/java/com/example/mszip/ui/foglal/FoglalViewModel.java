package com.example.mszip.ui.foglal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.mszip.model.service.Service;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoglalViewModel extends ViewModel {

    private final MutableLiveData<List<Service>> servicesLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Service>> getServices() {
        return servicesLiveData;
    }

    public void fetchServices() {
        db.collection("Services")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> serviceList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        Long price = doc.getLong("price");
                        String time = doc.getString("time");

                        if (name != null && price != null && time != null) {
                            serviceList.add(new Service(id, name, price.intValue(), time));
                        }
                    }
                    servicesLiveData.setValue(serviceList);
                })
                .addOnFailureListener(e -> servicesLiveData.setValue(new ArrayList<>()));
    }
}
