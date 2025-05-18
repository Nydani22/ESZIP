package com.example.mszip.model.service;

public class Service {
    public String id;

    public String name;
    public int price;
    public String time;

    public Service() {}

    public Service(String id, String name, int price, String time) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return  name + "\n Ára:" + price + " Ft\n Idő: " + time;
    }
}

