package com.fongmi.android.tv.utils;

import android.text.TextUtils;

import com.github.catvod.net.OkHttp;

public class Github {

    public static final String GITEE = "https://gitee.com/yang-dengfeng007/tvbox_-android_fongmi/raw/main";
    public static final String GITHUB = "https://raw.githubusercontent.com/Young0417/TVBOX_Android_fongmi/main";

    private static final String[] BASE_URLS = {GITEE, GITHUB};

    private static volatile String base = GITHUB;

    public static String getBase() {
        return base;
    }

    public static String fetch(String path) throws Exception {
        Exception error = null;
        for (String item : BASE_URLS) {
            try {
                String result = OkHttp.string(item + path).trim();
                if (isValid(result)) {
                    base = item;
                    return result;
                }
            } catch (Exception e) {
                error = e;
            }
        }
        if (error != null) throw error;
        throw new Exception("fetch failed: " + path);
    }

    public static String getJson(String name) throws Exception {
        return fetch("/apk/" + name + ".json");
    }

    public static String getApk(String name) {
        return getBase() + "/apk/" + name + ".apk";
    }

    private static boolean isValid(String result) {
        return !TextUtils.isEmpty(result) && !result.startsWith("[session") && !result.contains("Route error") && !result.contains("Access denied");
    }
}
