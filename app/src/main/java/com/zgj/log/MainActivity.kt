package com.zgj.log

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zgj.jlog.JLog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 0..100) {
            JLog.d("MyTag", "this is my test log d , i = $i")
        }
    }
}
