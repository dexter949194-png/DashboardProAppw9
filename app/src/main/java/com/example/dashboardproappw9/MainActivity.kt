package com.example.dashboardproappw9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btn = Button(this)
        btn.text = "Skanuj BLE"
        btn.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
        setContentView(btn)
    }
}
