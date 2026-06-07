package com.fongmi.android.tv.utils;

import android.text.TextUtils;

import com.github.catvod.net.OkHttp;

public class Github {

    public static final String GITHUB = "https://raw.githubusercontent.com/Young0417/TVBOX_Android_fongmi/main";

    private static final Source[] SOURCES = {
            new Source(
                    "https://ghfast.top/https://raw.githubusercontent.com/Young0417/TVBOX_Android_fongmi/main",
                    "https://ghfast.top/https://github.com/Young0417/TVBOX_Android_fongmi/raw/main"
            ),
            new Source(GITHUB, GITHUB)
    };

    private static volatile Source source = SOURCES[1];

    public static String getBase() {
        return source.apkBase;
    }

    public static String fetch(String path) throws Exception {
        Exception error = null;
        for (Source item : SOURCES) {
            try {
                String result = OkHttp.string(item.jsonBase + path).trim();
                if (isValid(result)) {
                    source = item;
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
        return source.apkBase + "/apk/" + name + ".apk";
    }

    private static boolean isValid(String result) {
        return !TextUtils.isEmpty(result) && !result.startsWith("[session") && !result.contains("Route error") && !result.contains("Access denied");
    }

    private static class Source {

        private final String jsonBase;
        private final String apkBase;

        private Source(String jsonBase, String apkBase) {
            this.jsonBase = jsonBase;
            this.apkBase = apkBase;
        }
    }
}
