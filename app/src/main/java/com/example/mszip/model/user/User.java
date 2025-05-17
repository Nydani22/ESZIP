package com.example.mszip.model.user;

public class User {
    public String id, email, teljesnev, role;

    public User(String id, String email, String teljesnev,String role) {
        this.id = id;
        this.email = email;
        this.teljesnev = teljesnev;
        this.role = role;
    }
    public User() {}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTeljesnev() {
        return teljesnev;
    }

    public void setTeljesnev(String teljesnev) {
        this.teljesnev = teljesnev;
    }
}
