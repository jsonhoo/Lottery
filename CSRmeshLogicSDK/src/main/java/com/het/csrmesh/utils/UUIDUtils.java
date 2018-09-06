package com.het.csrmesh.utils;

import android.content.Context;
import android.provider.Settings;

import java.util.UUID;

/**
 *
 */
public class UUIDUtils {

    private static String DEAULT_CONTROLLER_UUID_FIRST_PART = "346e0676-f22f-404e-";

    public static UUID getControllerUUID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uuidSecondPart = androidId.substring(0, 4) + "-" + androidId.substring(4, androidId.length());
        return UUID.fromString(DEAULT_CONTROLLER_UUID_FIRST_PART + uuidSecondPart);
    }
}
