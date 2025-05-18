package com.example.mszip.model.idopont;

public class Idopont {
    public String id, date /* év-hónap-nap */, serviceid;
    public boolean available;

    public Idopont() {
    }

    public Idopont(String id, String date, String serviceid, boolean available) {
        this.id = id;
        this.date = date;
        this.serviceid = serviceid;
        this.available = available;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


}
