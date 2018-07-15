package com.zgj.log

import android.app.Application
import android.os.Environment
import com.zgj.jlog.JLog
import com.zgj.jlog.LogConfig
import java.io.File

/**
 * Created by Mr Zhang on 2018/7/15.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        /*
        这里由于要使用到sd卡的读写权限,所以我将目标版本设置为了21,免去权限检测,若使用的是Android 6.0以上的版本,
        就需要申请此权限.
         */

        val logFileDir = Environment.getExternalStorageDirectory().absolutePath + File.separator + packageName +
                File.separator + "myLog"

        // 配置JLog
        val logConfig: LogConfig = LogConfig.builder()
                .context(applicationContext)
                .isSaveLog(true)
                .logLevel(LogConfig.LEVEL_DEBUG)
                .logSaveTimeInDays(2)
                .logFileDir(logFileDir)
                .build()
        JLog.setLogConfig(logConfig)
    }
}