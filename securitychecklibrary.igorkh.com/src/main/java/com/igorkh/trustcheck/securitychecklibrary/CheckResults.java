package com.igorkh.trustcheck.securitychecklibrary;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by igor khokhlov on 11/14/2017.
 */

public class CheckResults {

    public final static String SCREEN_LOCK = "Screen lock";
    public final static String UNKNOWN_SOURCES = "Unknown sources";
    public final static String POTENTIALLY_HURMFUL_APPS = "Potentially harmful applications";
    public final static String DEVELOPER_MENU = "Developer menu";
    public final static String BASIC_INTEGRITY = "Basic integrity test";
    public final static String COMPATIBILITY_TEST = "Android compatibility test";
    public final static String ANDROID_VERSION = "Android OS version";

    private boolean screenlock, unknsrc, pha, devmenu, bit, act;
    private byte androidVersion;

    public CheckResults() {
    }

    public CheckResults(boolean screenlock, boolean unknsrc, boolean pha, boolean devmenu, boolean bit, boolean act, byte androidVersion) {
        this.screenlock = screenlock;
        this.unknsrc = unknsrc;
        this.pha = pha;
        this.devmenu = devmenu;
        this.bit = bit;
        this.act = act;
        this.androidVersion = androidVersion;
    }

    public boolean isScreenlock() {
        return screenlock;
    }

    public void setScreenlock(boolean screenlock) {
        this.screenlock = screenlock;
    }

    public boolean isUnknsrc() {
        return unknsrc;
    }

    public void setUnknsrc(boolean unknsrc) {
        this.unknsrc = unknsrc;
    }

    public boolean isPha() {
        return pha;
    }

    public void setPha(boolean pha) {
        this.pha = pha;
    }

    public boolean isDevmenu() {
        return devmenu;
    }

    public void setDevmenu(boolean devmenu) {
        this.devmenu = devmenu;
    }

    public boolean isBit() {
        return bit;
    }

    public void setBit(boolean bit) {
        this.bit = bit;
    }

    public boolean isAct() {
        return act;
    }

    public void setAct(boolean act) {
        this.act = act;
    }

    public byte getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(byte androidVersion) {
        this.androidVersion = androidVersion;
    }

    public JSONObject getJSONresult(){
        JSONObject result = new JSONObject();

        try {
            result.put(SCREEN_LOCK,screenlock);
            result.put(UNKNOWN_SOURCES,unknsrc);
            result.put(POTENTIALLY_HURMFUL_APPS,pha);
            result.put(DEVELOPER_MENU,devmenu);
            result.put(BASIC_INTEGRITY,bit);
            result.put(COMPATIBILITY_TEST,act);
            result.put(ANDROID_VERSION,androidVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }
}
