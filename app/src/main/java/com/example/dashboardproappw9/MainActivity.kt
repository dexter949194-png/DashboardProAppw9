package com.example.dashboardproappw9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val btnScan = findViewById<Button>(R.id.btnScan)
            btnScan?.setOnClickListener {
                val intent = Intent(this, ScanActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Brak przycisku Scan w layout!", Toast.LENGTH_LONG).show()
        }
    }
}
