package com.example.mszip.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mszip.model.FoglalasViewModel;
import com.example.mszip.model.foglalas.Foglalas;
import com.example.mszip.model.idopont.Idopont;
import com.example.mszip.model.service.Service;
import com.example.mszip.service.FoglalasService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> teljesnev = new MutableLiveData<>();
    private final MutableLiveData<String> hibaUzenet = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sikeresMentes = new MutableLiveData<>();


    private final MutableLiveData<List<FoglalasViewModel>> foglalasok = new MutableLiveData<>();

    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getTeljesnev() { return teljesnev; }
    public LiveData<String> getHibaUzenet() { return hibaUzenet; }
    public LiveData<Boolean> getSikeresMentes() { return sikeresMentes; }

    public LiveData<List<FoglalasViewModel>> getFoglalasok() { return foglalasok; }



    private final FoglalasService service = new FoglalasService();




    public void betoltFoglalasok() {
        String uid = auth.getCurrentUser().getUid();
        service.getFoglalasokByUserId(uid, new FoglalasService.Callback<>() {
            @Override
            public void onSuccess(List<Foglalas> fList) {
                List<FoglalasViewModel> vmList = new ArrayList<>();
                if (fList.isEmpty()) {
                    foglalasok.postValue(vmList);
                    return;
                }
                for (Foglalas f : fList) {
                    service.getIdopontById(f.getIdopontid(), new FoglalasService.Callback<>() {
                        @Override
                        public void onSuccess(Idopont idop) {
                            service.getServiceById(idop.getServiceid(), new FoglalasService.Callback<>() {
                                @Override
                                public void onSuccess(Service sm) {
                                    vmList.add(new FoglalasViewModel(
                                            f.getId(),
                                            idop.getDate(),
                                            sm.getName(),
                                            String.valueOf(sm.getPrice())
                                    ));
                                    Collections.sort(vmList, (a, b) -> b.getDatum().compareTo(a.getDatum()));
                                    foglalasok.postValue(new ArrayList<>(vmList));
                                }
                                @Override public void onFailure(Exception e) { /* todo */ }
                            });
                        }
                        @Override public void onFailure(Exception e) { /* todo */ }
                    });
                }
            }
            @Override public void onFailure(Exception e) { /* todo */ }
        });
    }


    public void torolFoglalas(String foglalasId) {
        db.collection("Foglalasok").document(foglalasId).get()
                .addOnSuccessListener(docSnap -> {
                    if (!docSnap.exists()) {
                        betoltFoglalasok();
                        return;
                    }
                    String idopontId = docSnap.getString("idopontid");
                    if (idopontId == null) {
                        //todo hiba
                        return;
                    }

                    db.collection("Idopontok").document(idopontId)
                            .update("available", true)
                            .addOnSuccessListener(aVoid -> {
                                deleteBookingOnly(foglalasId);
                            })
                            .addOnFailureListener(e -> {
                                //todo hiba
                            });
                })
                .addOnFailureListener(e -> {
                    //todo hiba
                });
    }


    private void deleteBookingOnly(String foglalasId) {
        db.collection("Foglalasok").document(foglalasId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    betoltFoglalasok();
                })
                .addOnFailureListener(e -> {
                    hibaUzenet.setValue("Törlés hiba: " + e.getMessage());
                });
    }

    public void betoltAdatok() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            email.setValue(user.getEmail());
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> teljesnev.setValue(doc.getString("teljesnev")))
                    .addOnFailureListener(e -> hibaUzenet.setValue("Hiba: " + e.getMessage()));
        }
    }

    public void mentTeljesnev(String ujNev, String jelszo) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) return;


        auth.signInWithEmailAndPassword(user.getEmail(), jelszo)
                .addOnSuccessListener(result -> db.collection("Users").document(user.getUid())
                        .update("teljesnev", ujNev)
                        .addOnSuccessListener(unused -> {
                            sikeresMentes.setValue(true);
                        })
                        .addOnFailureListener(e -> hibaUzenet.setValue("Mentési hiba: " + e.getMessage()))
                ).addOnFailureListener(e -> hibaUzenet.setValue("Hibás jelszó!"));
    }

    public void torolFelh() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("Foglalasok")
                    .whereEqualTo("userid", uid)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String foglalasId = document.getId();
                            String idopontId = document.getString("idopontid");
                            if (idopontId != null) {
                                db.collection("Idopontok").document(idopontId)
                                        .update("available", true);
                            }
                            db.collection("Foglalasok").document(foglalasId)
                                    .delete();
                        }
                        db.collection("Users").document(uid).delete()
                                .addOnSuccessListener(aVoid -> {
                                    user.delete()
                                            .addOnSuccessListener(unused -> hibaUzenet.setValue("Fiók törölve"))
                                            .addOnFailureListener(e -> hibaUzenet.setValue("Felhasználó törlés sikertelen: " + e.getMessage()));
                                })
                                .addOnFailureListener(e -> hibaUzenet.setValue("Adatbázisból törlés hiba: " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> hibaUzenet.setValue("Foglalások törlése sikertelen: " + e.getMessage()));
        }
    }

}
