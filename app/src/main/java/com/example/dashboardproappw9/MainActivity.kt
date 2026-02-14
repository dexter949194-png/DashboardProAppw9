package com.example.dashboardproappw9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Crash handler
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val intent = Intent(this, CrashActivity::class.java)
            intent.putExtra("error", buildString {
                appendLine("Exception: ${throwable.javaClass.simpleName}")
                appendLine("Message: ${throwable.message}")
                appendLine()
                appendLine("Stacktrace:")
                throwable.stackTrace.forEach {
                    appendLine(it.toString())
                }
            })
            startActivity(intent)
            finish()
        }

        // Button to trigger next
        val btn = Button(this).apply { text = "Start Scan" }
        btn.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
        setContentView(btn)
    }
}
