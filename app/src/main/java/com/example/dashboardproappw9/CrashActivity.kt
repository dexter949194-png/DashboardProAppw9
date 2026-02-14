package com.example.dashboardproappw9

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this)
        tv.setPadding(16, 16, 16, 16)
        tv.textSize = 14f

        val error = intent.getStringExtra("error")
        tv.text = "Aplikacja Crasha!\n\n$error"

        setContentView(tv)
    }
}
