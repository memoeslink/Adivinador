package com.app.memoeslink.adivinador;

import org.memoeslink.StringHelper;

public class AdUnitId {
    public static final String APP_OPEN_ID = "";
    public static final String BANNER_ID = "ca-app-pub-9370416997367495/4952493068";
    public static final String INTERSTITIAL_ID = "ca-app-pub-9370416997367495/9592695166";
    public static final String INTERSTITIAL_VIDEO_ID = "";
    public static final String REWARDED_VIDEO_ID = "";
    public static final String NATIVE_ADVANCED_ID = "";
    public static final String NATIVE_ADVANCED_VIDEO_ID = "";
    public static final String TEST_APP_OPEN_ID = "ca-app-pub-3940256099942544/3419835294";
    public static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String TEST_INTERSTITIAL_VIDEO_ID = "ca-app-pub-3940256099942544/8691691433";
    public static final String TEST_REWARDED_VIDEO_ID = "ca-app-pub-3940256099942544/5224354917";
    public static final String TEST_NATIVE_ADVANCED_ID = "ca-app-pub-3940256099942544/2247696110";
    public static final String TEST_NATIVE_ADVANCED_VIDEO_ID = "ca-app-pub-3940256099942544/1044960115";

    public static String getAppOpenId() {
        if (BuildConfig.DEBUG)
            return TEST_APP_OPEN_ID;
        return StringHelper.defaultIfBlank(APP_OPEN_ID, TEST_APP_OPEN_ID);
    }

    public static String getBannerId() {
        if (BuildConfig.DEBUG)
            return TEST_BANNER_ID;
        return StringHelper.defaultIfBlank(BANNER_ID, TEST_BANNER_ID);
    }

    public static String getInterstitialId() {
        if (BuildConfig.DEBUG)
            return TEST_INTERSTITIAL_ID;
        return StringHelper.defaultIfBlank(INTERSTITIAL_ID, TEST_INTERSTITIAL_ID);
    }

    public static String getInterstitialVideoId() {
        if (BuildConfig.DEBUG)
            return TEST_INTERSTITIAL_VIDEO_ID;
        return StringHelper.defaultIfBlank(INTERSTITIAL_VIDEO_ID, TEST_INTERSTITIAL_VIDEO_ID);
    }

    public static String getRewardedVideoId() {
        if (BuildConfig.DEBUG)
            return TEST_REWARDED_VIDEO_ID;
        return StringHelper.defaultIfBlank(REWARDED_VIDEO_ID, TEST_REWARDED_VIDEO_ID);
    }

    public static String getNativeAdvancedId() {
        if (BuildConfig.DEBUG)
            return TEST_NATIVE_ADVANCED_ID;
        return StringHelper.defaultIfBlank(NATIVE_ADVANCED_ID, TEST_NATIVE_ADVANCED_ID);
    }

    public static String getNativeAdvancedVideoId() {
        if (BuildConfig.DEBUG)
            return TEST_NATIVE_ADVANCED_VIDEO_ID;
        return StringHelper.defaultIfBlank(NATIVE_ADVANCED_VIDEO_ID, TEST_NATIVE_ADVANCED_VIDEO_ID);
    }
}
