package com.example.mszip.service;

import com.example.mszip.model.foglalas.Foglalas;
import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoglalasService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }


    public void getFoglalasokByUserId(String userId, Callback<List<Foglalas>> callback) {
        db.collection("Foglalasok")
                .whereEqualTo("userid", userId)
                .get()
                .addOnSuccessListener((QuerySnapshot snap) -> {
                    List<Foglalas> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Foglalas f = doc.toObject(Foglalas.class);
                        f.setId(doc.getId());
                        lista.add(f);
                    }
                    callback.onSuccess(lista);
                })
                .addOnFailureListener(callback::onFailure);
    }


    public void getIdopontById(String idopontId, Callback<Idopont> callback) {
        db.collection("Idopontok").document(idopontId)
                .get()
                .addOnSuccessListener(doc -> callback.onSuccess(doc.toObject(Idopont.class)))
                .addOnFailureListener(callback::onFailure);
    }




    public void getServiceById(String serviceId, Callback<Service> callback) {
        db.collection("Services").document(serviceId)
                .get()
                .addOnSuccessListener(doc -> callback.onSuccess(doc.toObject(Service.class)))
                .addOnFailureListener(callback::onFailure);
    }
}
