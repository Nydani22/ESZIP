package com.example.mszip.model.idopont;

public class Idopont {
    public String id, date /* év-hónap-nap */, serviceid;

    public Idopont() {
    }

    public Idopont(String id, String date, String serviceid) {
        this.id = id;
        this.date = date;
        this.serviceid = serviceid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }
}
