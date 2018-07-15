package com.zgj.jlog;

/**
 * Created by Mr Zhang on 2018/7/15.
 */

public class JLog {
    private static LogConfig mLogConfig = LogConfig.builder().build();

    private JLog() {}

    public static void setLogConfig(LogConfig logConfig) {
        mLogConfig = logConfig;
    }

    public static void v(String tag, String msg) {
        mLogConfig.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        mLogConfig.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        mLogConfig.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        mLogConfig.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        mLogConfig.e(tag, msg);
    }
}
