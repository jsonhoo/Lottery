package com.fsix.mqtt.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ATil {
    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(final Context context) {
        String tmDevice = "";
        String tmSerial = "";
        String deviceid = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceid = deviceUuid.toString();
        } catch (Exception ex) {
            Logc.d("get imei error", ex.toString());
        }

        return deviceid;
    }

    private static String getMacAddr() {
        String eth0 = null;
        String wlan0 = null;
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            res1.append(String.format("%02X:", b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        wlan0 = res1.toString().replace(":", "").trim();
                    }
                }

                if (nif.getName().equalsIgnoreCase("eth0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes != null) {
                        StringBuilder res1 = new StringBuilder();
                        for (byte b : macBytes) {
                            res1.append(String.format("%02X:", b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        wlan0 = res1.toString().replace(":", "").trim();
                    }
                }
                /*if (!nif.getName().equalsIgnoreCase("wlan0"))//wlan0
                    continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().replace(":", "").trim();*/
            }
        } catch (Exception ex) {
        }
        if (!TextUtils.isEmpty(wlan0)) {
            return wlan0;
        } else {
            return eth0;
        }
    }

    private static String getMacAddrOld(Context context) {
        String macString = "";
        WifiManager wifimsg = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifimsg != null) {
            if (wifimsg.getConnectionInfo() != null) {
                if (wifimsg.getConnectionInfo().getMacAddress() != null) {
                    macString = wifimsg.getConnectionInfo().getMacAddress();
                }
            }
        }
        System.out.println(" #### " + macString);
        return macString;
    }

    public static String getMac(Context context) {
        String addr = getMacAddrOld(context);
        if (TextUtils.isEmpty(addr) || addr.equals("02:00:00:00:00:00")) {
            addr = getMacAddr();
            System.out.println(" #### getDeviceMacAddress" + addr);
        }
        return addr.replaceAll(":", "");
    }

    /**
     * 从字节数组获取对象
     *
     * @EditTime 2007-8-13 上午11:46:34
     */
    public static Object getObjectFromBytes(byte[] objBytes) throws Exception {
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }

    /**
     * 从对象获取一个字节数组
     *
     * @EditTime 2007-8-13 上午11:46:56
     */
    public static byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    }
}
