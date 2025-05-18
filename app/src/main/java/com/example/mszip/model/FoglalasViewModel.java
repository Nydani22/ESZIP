package com.example.mszip.model;

public class FoglalasViewModel {
    private final String foglalasId;
    private final String datum;
    private final String szolgaltatasNev;
    private final String ar;

    public FoglalasViewModel(String foglalasId, String datum, String szolgaltatasNev, String ar) {
        this.foglalasId = foglalasId;
        this.datum = datum;
        this.szolgaltatasNev = szolgaltatasNev;
        this.ar = ar;
    }

    public String getFoglalasId() {
        return foglalasId;
    }

    public String getDatum() {
        return datum;
    }

    public String getSzolgaltatasNev() {
        return szolgaltatasNev;
    }

    public String getAr() {
        return ar;
    }


}
