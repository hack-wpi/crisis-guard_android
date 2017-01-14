package com.shltr.darrieng.shltr_android.Event;

import android.location.Location;

public class LocationRetrievedEvent {
    private Location location;

    public LocationRetrievedEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
