package com.example.mszip.service;

import com.example.mszip.model.idopont.Idopont;

public class IdopontWithService {
    public Idopont idopont;
    public String serviceName;

    public IdopontWithService(Idopont idopont, String serviceName) {
        this.idopont = idopont;
        this.serviceName = serviceName;
    }

    public Idopont getIdopont() {
        return idopont;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        return idopont.date + " - " + serviceName;
    }
}

