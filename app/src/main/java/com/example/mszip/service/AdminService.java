package com.example.mszip.service;

import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    public void getIdopontWithServiceNameById(String idopontId, Callback<IdopontWithService> callback) {
        db.collection("Idopontok").document(idopontId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onFailure(new Exception("Idopont nem található"));
                        return;
                    }
                    Idopont idopont = doc.toObject(Idopont.class);
                    idopont.setId(doc.getId());
                    db.collection("Services").document(idopont.getServiceid())
                            .get()
                            .addOnSuccessListener(serviceDoc -> {
                                Service s = serviceDoc.toObject(Service.class);
                                String name = (s != null) ? s.getName() : "Ismeretlen";
                                callback.onSuccess(new IdopontWithService(idopont, name));
                            })
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }
    /*
    public void getAllIdopontWithServiceNames(Callback<List<IdopontWithService>> callback) {
        db.collection("Idopontok")
                .get()
                .addOnSuccessListener(snap -> {
                    List<IdopontWithService> resultList = new ArrayList<>();
                    List<Idopont> idopontok = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Idopont i = doc.toObject(Idopont.class);
                        i.setId(doc.getId());
                        idopontok.add(i);
                    }

                    if (idopontok.isEmpty()) {
                        callback.onSuccess(resultList);
                        return;
                    }

                    List<IdopontWithService> tempList = new ArrayList<>();
                    int[] counter = {0};

                    for (Idopont idopont : idopontok) {
                        db.collection("Services").document(idopont.getServiceid())
                                .get()
                                .addOnSuccessListener(serviceDoc -> {
                                    Service s = serviceDoc.toObject(Service.class);
                                    String name = (s != null) ? s.getName() : "Ismeretlen";
                                    tempList.add(new IdopontWithService(idopont, name));
                                    counter[0]++;
                                    if (counter[0] == idopontok.size()) {
                                        callback.onSuccess(tempList);
                                    }
                                })
                                .addOnFailureListener(callback::onFailure);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

     */

}
