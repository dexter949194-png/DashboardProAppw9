package com.example.dashboardproappw9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Global crash handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val intent = Intent(this, CrashActivity::class.java)
            intent.putExtra("error", throwable.toString() + "\n\n" + throwable.stackTrace.joinToString("\n"))
            startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prosty UI i przycisk do skanowania
        val btnScan = findViewById<Button>(R.id.btnScan)
        btnScan.setOnClickListener {
            try {
                val intent = Intent(this, ScanActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
