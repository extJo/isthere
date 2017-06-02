package com.apptive.joDuo.isthere;


import android.util.Log;

/**
 * Created by zeroho on 2017. 6. 2..
 */

public class LogDebuger {
    private static boolean isDebugging = false;

    private static boolean httpHelper = false;
    private static boolean loginPage = true;

    enum TagType {
        HTTP_HELPER, LOGIN_PAGE
    }

    public static void debugPrinter(TagType type, String str) {
        if (isDebugging || getBoolean(type)) {
            Log.d(convertToString(type), str);
        }
    }

    private static String convertToString(TagType type) {
        String result = "undefined";

        switch (type) {
            case HTTP_HELPER: result = "HttpHelper"; break;
            case LOGIN_PAGE: result = "LoginPage"; break;
        }

        return result;
    }

    private static boolean getBoolean(TagType type) {
        boolean result = false;

        switch (type) {
            case HTTP_HELPER: result = httpHelper; break;
            case LOGIN_PAGE: result = loginPage; break;
        }

        return result;
    }

}
