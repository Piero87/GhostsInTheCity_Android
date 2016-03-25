package com.ghostsinthecity_android;

import android.location.Location;

public interface LocationEvent {

    /**
     * LocationEvent listener method that notify when the gps position change
     *
     * @param location Location Entity
     */
    void updateLocation(Location location);
}
