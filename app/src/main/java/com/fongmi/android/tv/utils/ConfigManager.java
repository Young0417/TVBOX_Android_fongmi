package com.fongmi.android.tv.utils;

import android.os.Looper;
import android.text.TextUtils;

import com.fongmi.android.tv.App;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Response;

public class ConfigManager {

    private static final String TAG = ConfigManager.class.getSimpleName();
    private static final String CONFIG_FILE = "config.txt";
    private static final String REMOTE_CONFIG_TXT = "https://gitee.com/yang-dengfeng007/tvbox_-android_fongmi/raw/main/app/src/main/assets/config.txt";
    private static final String DEFAULT_URL = "http://www.饭太硬.com/tv";

    private static class Loader {
        static volatile ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager get() {
        return Loader.INSTANCE;
    }

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public String getDefaultUrl() {
        return getDefaultUrl(CONFIG_FILE);
    }

    public String getDefaultUrl(String fileName) {
        if (cache.containsKey(fileName)) {
            return cache.get(fileName);
        }

        String url = resolveUrl(fileName);
        url = url.trim();
        cache.put(fileName, url);
        return url;
    }

    public void clearCache() {
        cache.clear();
    }

    private String resolveUrl(String fileName) {
        String url = null;
        if (!isMainThread()) {
            url = readFromRemote(REMOTE_CONFIG_TXT);
        }
        if (TextUtils.isEmpty(url)) {
            url = readFromExternalStorage(fileName);
        }
        if (TextUtils.isEmpty(url)) {
            url = readFromAssets(fileName);
        }
        if (TextUtils.isEmpty(url)) {
            url = DEFAULT_URL;
        }
        return url;
    }

    private String readFromRemote(String url) {
        try (Response res = OkHttp.newCall(UrlUtil.convert(url.trim()), TAG).execute()) {
            if (res.isSuccessful() && res.body() != null) {
                String content = res.body().string().trim();
                if (!TextUtils.isEmpty(content)) return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private String readFromExternalStorage(String fileName) {
        File file = Path.files(fileName);
        if (file.exists() && file.canRead()) {
            try {
                return FileUtil.readTextFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String readFromAssets(String fileName) {
        try (InputStream is = App.get().getAssets().open(fileName)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
