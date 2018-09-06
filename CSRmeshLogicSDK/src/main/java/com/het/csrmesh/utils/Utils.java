package com.het.csrmesh.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.het.csrmesh.App;
import com.het.csrmesh.events.MeshSystemEvent;

public class Utils {

    static final String LATEST_PLACE_ID_KEY = "LATEST_PLACE_ID_KEY";
    static final String PREFERENCES_KEY = "CSR_PREFERENCES_KEY";

    static public String getLatestPlaceIdUsed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_KEY, context.MODE_PRIVATE);
        return prefs.getString(LATEST_PLACE_ID_KEY, "");
    }

    static public void setLatestPlaceIdUsed(Context context, String id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_KEY, context.MODE_PRIVATE).edit();
        editor.putString(LATEST_PLACE_ID_KEY, id);
        editor.commit();
        App.bus.post(new MeshSystemEvent(MeshSystemEvent.SystemEvent.PLACE_CHANGED));
    }
}
