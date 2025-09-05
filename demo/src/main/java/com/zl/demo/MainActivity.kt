package com.zl.demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.view.captchaedittext.CaptchaEditText
import com.view.demo.R

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout_main)
        findViewById<CaptchaEditText>(R.id.test1).setCallback {
                code ->
            Toast.makeText(MainActivity@this, "验证码：$code", Toast.LENGTH_SHORT).show()
        }
        findViewById<CaptchaEditText>(R.id.test2).setCallback {
            code ->
            Toast.makeText(MainActivity@this, "验证码：$code", Toast.LENGTH_SHORT).show()
        }
    }
}
