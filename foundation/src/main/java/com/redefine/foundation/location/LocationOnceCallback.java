package com.redefine.foundation.location;

import android.location.Location;

public interface LocationOnceCallback {

    void onLocateOnce(Location location);
    void onLocateOnceFailed();

}