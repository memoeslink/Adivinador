package com.app.memoeslink.adivinador;

import org.apache.commons.lang3.StringUtils;

public class AdUnitId {
    public static final String APP_OPEN = "";
    public static final String BANNER = "ca-app-pub-9370416997367495/4952493068";
    public static final String INTERSTITIAL = "ca-app-pub-9370416997367495/9592695166";
    public static final String INTERSTITIAL_VIDEO = "";
    public static final String REWARDED_VIDEO = "";
    public static final String NATIVE_ADVANCED = "";
    public static final String NATIVE_ADVANCED_VIDEO = "";
    public static final String TEST_APP_OPEN = "ca-app-pub-3940256099942544/3419835294";
    public static final String TEST_BANNER = "ca-app-pub-3940256099942544/6300978111";
    public static final String TEST_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";
    public static final String TEST_INTERSTITIAL_VIDEO = "ca-app-pub-3940256099942544/8691691433";
    public static final String TEST_REWARDED_VIDEO = "ca-app-pub-3940256099942544/5224354917";
    public static final String TEST_NATIVE_ADVANCED = "ca-app-pub-3940256099942544/2247696110";
    public static final String TEST_NATIVE_ADVANCED_VIDEO = "ca-app-pub-3940256099942544/1044960115";

    public static String getAppOpenId() {
        if (BuildConfig.DEBUG)
            return TEST_APP_OPEN;
        return StringUtils.defaultString(APP_OPEN, TEST_APP_OPEN);
    }

    public static String getBannerId() {
        if (BuildConfig.DEBUG)
            return TEST_BANNER;
        return StringUtils.defaultIfBlank(BANNER, TEST_BANNER);
    }

    public static String getInterstitialId() {
        if (BuildConfig.DEBUG)
            return TEST_INTERSTITIAL;
        return StringUtils.defaultIfBlank(INTERSTITIAL, TEST_INTERSTITIAL);
    }

    public static String getInterstitialVideoId() {
        if (BuildConfig.DEBUG)
            return TEST_INTERSTITIAL_VIDEO;
        return StringUtils.defaultIfBlank(INTERSTITIAL_VIDEO, TEST_INTERSTITIAL_VIDEO);
    }

    public static String getRewardedVideoId() {
        if (BuildConfig.DEBUG)
            return TEST_REWARDED_VIDEO;
        return StringUtils.defaultIfBlank(REWARDED_VIDEO, TEST_REWARDED_VIDEO);
    }

    public static String getNativeAdvancedId() {
        if (BuildConfig.DEBUG)
            return TEST_NATIVE_ADVANCED;
        return StringUtils.defaultIfBlank(NATIVE_ADVANCED, TEST_NATIVE_ADVANCED);
    }

    public static String getNativeAdvancedVideoId() {
        if (BuildConfig.DEBUG)
            return TEST_NATIVE_ADVANCED_VIDEO;
        return StringUtils.defaultIfBlank(NATIVE_ADVANCED_VIDEO, TEST_NATIVE_ADVANCED_VIDEO);
    }
}
