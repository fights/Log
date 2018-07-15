# JLog

JLog是一个好用的Android Log记录工具。Log是日常开发中一种常用的调试工具，因此一个好用的Log记录工具对开发效率是非常
有用的。

# 开始使用

## 配置依赖
第一步是将JLog配置到您的项目中，如下：

使用gradle，配置如下：

    Step 1. Add the JitPack repository to your build file

    allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}

    Step 2. Add the dependency

    dependencies {
    	        implementation 'com.github.fights:Log:V1.0.0'
    	}

使用Maven，配置如下：

    Step 1. Add the JitPack repository to your build file

    <repositories>
    		<repository>
    		    <id>jitpack.io</id>
    		    <url>https://jitpack.io</url>
    		</repository>
    	</repositories>

    Step 2. Add the dependency

    <dependency>
    	    <groupId>com.github.fights</groupId>
    	    <artifactId>Log</artifactId>
    	    <version>V1.0.0</version>
    	</dependency>

## 参数配置

使用JLog，若不进行配置，则会使用默认配置，所有的日志都将会打印，并且日志不会保存。

若想要更多的配置，可参考如下：

    /*
    注意：若设置保存日志，则需要如下权限：
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    若使用Android 6.0以上，需要进行权限动态申请。
    */

    val logFileDir = Environment.getExternalStorageDirectory().absolutePath + File.separator + packageName +
                    File.separator + "myLog"

    // 配置JLog
    val logConfig: LogConfig = LogConfig.builder()
            // 配置上下文环境，若配置保存日志并且没有指定具体路径，则上下文环境必须配置，并且要在isSaveLog前面
            // 默认的日志路径为：/mnt/sdcard/jLog/包名/
            .context(applicationContext)
            // 配置保存日志，日志名称为当前时间，格式为20150213，且一天的日志为一个文件
            .isSaveLog(true)
            // 配置日志等级，共五种，和Android原生日志等级相对应，设置了之后，只会打印和保存相对应等级以及后续的等级
            // 的日志。如设置了LEVEL_INFO，则只会打印LEVEL_INFO、LEVEL_WARN、LEVEL_ERROR的日志。
            .logLevel(LogConfig.LEVEL_DEBUG)
            // 日志保存时间，单位是天，如设置为2天，则当天的日志在两天后会被删除。
            .logSaveTimeInDays(2)
            // 设置日志保存路径，也可以不设置，使用默认路径
            .logFileDir(logFileDir)
            .build()
    JLog.setLogConfig(logConfig)

## 使用

    JLog.v("MyTag", "this is my test log d , i = $i")
    JLog.d("MyTag", "this is my test log d , i = $i")
    JLog.i("MyTag", "this is my test log d , i = $i")
    JLog.w("MyTag", "this is my test log d , i = $i")
    JLog.e("MyTag", "this is my test log d , i = $i")