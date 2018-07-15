package com.zgj.jlog;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mr Zhang on 2018/7/15.
 */

final public class LogConfig {

    private static final String TAG = LogConfig.class.getSimpleName();
    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARN = 3;
    public static final int LEVEL_ERROR = 4;
    private static final String LEVEL_V = "V";
    private static final String LEVEL_D = "D";
    private static final String LEVEL_I = "I";
    private static final String LEVEL_W = "W";
    private static final String LEVEL_E = "E";
    private static final int HANDLER_WHAT_CHECK_LOG_FILE_EXPIRED = 0;
    private static final int HANDLER_WHAT_SAVE_LOG = 1;
    private static final String KEY_TIME = "key_time";
    private static final String KEY_LEVEL = "key_level";
    private static final String KEY_TAG = "key_tag";
    private static final String KEY_MSG = "key_msg";
    private final Builder builder;
    private final HandlerThread logThread;
    private Handler logHandler;
    private SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
    private File logFolder;

    /**
     * 记录当前文本保存的文件的名称，也就是当天的日期。
     */
    private String currentTime ;
    private File logFile;

    private LogConfig(Builder builder) {
        this.builder = builder;
        logThread = new HandlerThread("JTLog_thread");
        logThread.start();
        createLogHandler();
    }

    private void createLogHandler() {
        logHandler = new Handler(logThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_WHAT_CHECK_LOG_FILE_EXPIRED:

                        // 检测超过目标日期的日志，并删除
                        checkLogFileExpired();
                        break;

                    case HANDLER_WHAT_SAVE_LOG:
                        Bundle bundle = (Bundle) msg.obj;

                        //保存日志到文件中
                        saveLogToFile(bundle);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void saveLogToFile(Bundle bundle) {
        long time = bundle.getLong(KEY_TIME);
        String level = bundle.getString(KEY_LEVEL);
        String msg = bundle.getString(KEY_MSG);
        String tag = bundle.getString(KEY_TAG);
        if(!Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
            Log.e(TAG, "SDCard未加载，无法把日志写入文件。");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(logDateFormat.format(new Date(time)));
            sb.append(": ");
            sb.append(level);
            sb.append("/");
            sb.append(tag);
            sb.append(": ");
            sb.append(msg);
            String logText = sb.toString();
            if (logFolder == null) {
                logFolder = new File(builder.logFileDir);
                if(!logFolder.exists()) {
                    boolean isSuccess = logFolder.mkdirs();
                    if(!isSuccess) {
                        Log.e(TAG, "saveLogToFile: create log dir failed" );
                        return;
                    }
                }
            }

            String nowTime = fileNameDateFormat.format(new Date());
            if(!nowTime.equals(currentTime)){
                String logFileName = builder.logFileDir + File.separator + nowTime;
                logFile = new File(logFileName);
                if(!logFile.exists()) {
                    try {
                        boolean isCreateSuccess = logFile.createNewFile();
                        if (isCreateSuccess) {
                            Log.e(TAG, "saveLogToFile: create log file failed" );
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentTime = nowTime;
            }
            PrintWriter pw = null;

            try {
                pw = new PrintWriter(new FileWriter(logFile, true));
                pw.println(logText);
                pw.flush();
            } catch (Exception var16) {
                var16.printStackTrace();
            } finally {
                if(pw != null) {
                    pw.close();
                }
            }

        }
    }

    private void checkLogFileExpired() {
        //根据日志的保存期限，将超过期限的日志删除
        if (TextUtils.isEmpty(builder.logFileDir))  return;

        File file = new File(builder.logFileDir);
        if(!file.exists()) return;

        File[] logFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().matches("[0-9]{8}");
            }
        });
        for (File logFile : logFiles) {
            String name = logFile.getName();
            int time = getFileTimeUtilNow(name);
            if (time > builder.logSaveTimeInDays) {
                boolean b = logFile.delete();
                Log.d(TAG, "log file delete result is " + b + ", name = " + name);
            }
        }
    }

    /**
     * 获取文件的日期到现在有多少天。
     *
     * @param name 日志文件名，是以日期为名字。
     * @return 天数
     */
    private int getFileTimeUtilNow(String name) {
        int time ;
        try {
            Date date = fileNameDateFormat.parse(name);
            time = (int) ((System.currentTimeMillis() - date.getTime()) / (24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            time = -1;
        }

        return time;
    }

    public static Builder builder() {
        return new Builder();
    }

    void v(String tag, String msg) {
        if(builder.logLevel <= LEVEL_VERBOSE) {
            Log.v(tag, msg);
            if(builder.isSaveLog) {
                long time = System.currentTimeMillis();
                processLog(time, LEVEL_V, tag, msg);
            }
        }

    }


    void d(String tag, String msg) {
        if(builder.logLevel <= LEVEL_DEBUG) {
            Log.d(tag, msg);
            if(builder.isSaveLog) {
                long time = System.currentTimeMillis();
                processLog(time, LEVEL_D, tag, msg);
            }
        }

    }

    void i(String tag, String msg) {
        if(builder.logLevel <= LEVEL_INFO) {
            Log.i(tag, msg);
            if(builder.isSaveLog) {
                long time = System.currentTimeMillis();
                processLog(time, LEVEL_I , tag, msg);
            }
        }

    }

    void w(String tag, String msg) {
        if(builder.logLevel <= LEVEL_WARN) {
            Log.w(tag, msg);
            if(builder.isSaveLog) {
                long time = System.currentTimeMillis();
                processLog(time, LEVEL_W, tag, msg);
            }
        }

    }

    void e(String tag, String msg) {
        if(builder.logLevel <= LEVEL_ERROR) {
            Log.e(tag, msg);
            if(builder.isSaveLog) {
                long time = System.currentTimeMillis();
                processLog(time, LEVEL_E, tag, msg);
            }
        }

    }

    private synchronized void processLog(long time, String level, String tag, String msg) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        message.what = HANDLER_WHAT_SAVE_LOG;
        bundle.putLong(KEY_TIME, time);
        bundle.putString(KEY_LEVEL, level);
        bundle.putString(KEY_TAG, tag);
        bundle.putString(KEY_MSG, msg);
        message.obj = bundle;
        logHandler.sendMessage(message);
    }
    public static class Builder{
        private static final String COMPANY_NAME = "jLog";
        String logFileDir;
        boolean isSaveLog = false;
        long logSaveTimeInDays = 30;
        int logLevel = 0;

        private Builder() {}

        public Builder logFileDir(String logFileDir) {
            this.logFileDir = logFileDir;
            return this;
        }

        public Builder isSaveLog(boolean isSaveLog) {

            //当设置为要保存日志时，要判断logFileDir参数是否有效。
            if (isSaveLog && TextUtils.isEmpty(logFileDir))
                throw new RuntimeException("请配置上下文或者配置日志保存路径");

            this.isSaveLog = isSaveLog;
            return this;
        }

        public Builder logSaveTimeInDays(int logSaveTimeInDays) {
            this.logSaveTimeInDays = logSaveTimeInDays;
            return this;
        }

        public Builder logLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder context(Context context) {
            logFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    COMPANY_NAME + File.separator + context.getPackageName();
            return this;
        }

        public LogConfig build() {
            return new LogConfig(this);
        }
    }
}
