package com.shltr.darrieng.shltr_android.Pojo;

public class FlarePojo {
    double longitude;

    double lat;

    int user_id;

    String type;

    public FlarePojo(double longitude, double lat, int user_id) {
        this.longitude = longitude;
        this.lat = lat;
        this.user_id = user_id;
        String type = "other";
    }

    public FlarePojo(double longitude, double lat, int user_id, String type) {
        this.longitude = longitude;
        this.lat = lat;
        this.user_id = user_id;
        this.type = type;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
