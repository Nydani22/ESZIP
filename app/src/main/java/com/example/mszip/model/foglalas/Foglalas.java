package com.example.mszip.model.foglalas;

public class Foglalas {
    public String id, userid, idopontid;

    public Foglalas(String id, String userid, String idopontid) {
        this.id = id;
        this.userid = userid;
        this.idopontid = idopontid;
    }

    public Foglalas() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getIdopontid() {
        return idopontid;
    }

    public void setIdopontid(String idopontid) {
        this.idopontid = idopontid;
    }
}
