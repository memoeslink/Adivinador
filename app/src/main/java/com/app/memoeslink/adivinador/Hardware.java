package com.app.memoeslink.adivinador;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.memoeslink.generator.common.CharHelper;
import com.memoeslink.generator.common.StringHelper;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Hardware extends BaseWrapper {
    private static final Field[] versionCodes;

    static {
        versionCodes = Build.VERSION_CODES.class.getFields();
    }

    public Hardware(Context context) {
        super(context);
    }

    public String getNetworkName() {
        if (!isNetworkAvailable())
            return ResourceFinder.RESOURCE_NOT_FOUND;

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = null;

        if (wifiManager != null)
            wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED)
            return StringHelper.trimToDefault(wifiInfo.getSSID());
        return ResourceFinder.RESOURCE_NOT_FOUND;
    }

    public String getNetworkOperator() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if ((manager != null ? manager.getSimState() : 0) == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkOperatorName();
        return ResourceFinder.RESOURCE_NOT_FOUND;
    }

    public String getNetworkCountry() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if ((manager != null ? manager.getSimState() : 0) == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkCountryIso();
        return ResourceFinder.RESOURCE_NOT_FOUND;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();

                    if (!inetAddress.isLoopbackAddress())
                        return inetAddress.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ResourceFinder.RESOURCE_NOT_FOUND;
    }

    public String getTestDeviceId() {
        return MD5(getDeviceId()).toUpperCase();
    }

    public String getDeviceId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (StringHelper.isNullOrBlank(androidId) || androidId.length() <= 4)
            return getDevicePseudoId();
        return StringHelper.mask(androidId);
    }

    public String getDevicePseudoId() {
        return String.format("%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c",
                CharHelper.getHexDigit(Build.BOARD.length()),
                CharHelper.getHexDigit(Build.BOOTLOADER.length()),
                CharHelper.getHexDigit(Build.BRAND.length()),
                CharHelper.getHexDigit(Build.DEVICE.length()),
                CharHelper.getHexDigit(Build.DISPLAY.length()),
                CharHelper.getHexDigit(Build.FINGERPRINT.length()),
                CharHelper.getHexDigit(Build.HARDWARE.length()),
                CharHelper.getHexDigit(Build.HOST.length()),
                CharHelper.getHexDigit(Build.ID.length()),
                CharHelper.getHexDigit(Build.MANUFACTURER.length()),
                CharHelper.getHexDigit(Build.MODEL.length()),
                CharHelper.getHexDigit(Build.PRODUCT.length()),
                CharHelper.getHexDigit(Build.TAGS.length()),
                CharHelper.getHexDigit(Build.TYPE.length()),
                CharHelper.getHexDigit(Build.USER.length())
        );
    }

    public String getAndroidVersionName() {
        if (versionCodes == null || versionCodes.length == 0)
            return ResourceFinder.RESOURCE_NOT_FOUND;

        for (Field versionCode : versionCodes) {
            String versionName = versionCode.getName();
            int fieldValue = -1;

            try {
                fieldValue = versionCode.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT)
                return versionName;
        }
        return ResourceFinder.RESOURCE_NOT_FOUND;
    }

    private String MD5(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();

            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }
}
