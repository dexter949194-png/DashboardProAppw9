package com.example.dashboardproappw9

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorText = intent.getStringExtra("error") ?: "Unknown crash"

        val tv = TextView(this).apply {
            textSize = 12f
            text = errorText
        }

        val scroll = ScrollView(this).apply {
            addView(tv)
        }

        setContentView(scroll)
    }
}
