package com.example.dashboardproappw9

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class ScanActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bleScanner: BluetoothLeScanner

    private val devices = mutableListOf<android.bluetooth.BluetoothDevice>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView  // <-- globalne pole

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tworzymy dynamiczny layout
        val btnScan = Button(this).apply { text = "Skanuj BLE" }
        listView = ListView(this)  // <-- teraz dostępne w całej klasie

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(btnScan)
            addView(listView)
        }

        setContentView(layout)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bleScanner = bluetoothAdapter.bluetoothLeScanner

        btnScan.setOnClickListener {
            checkPermissionsAndScan()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = devices[position]
            Toast.makeText(this, "Wybrano: ${device.name}", Toast.LENGTH_SHORT).show()

            // Przejście do GattActivity z MAC adresu
            val intent = Intent(this, GattActivity::class.java)
            intent.putExtra("MAC", device.address)
            startActivity(intent)
        }
    }

    private fun checkPermissionsAndScan() {
        val permissions = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
        } else {
            startBleScan()
        }
    }

    private fun startBleScan() {
        adapter.clear()
        devices.clear()

        bleScanner.startScan(scanCallback)
        Toast.makeText(this, "Skanowanie BLE...", Toast.LENGTH_SHORT).show()

        listView.postDelayed({
            bleScanner.stopScan(scanCallback)
            Toast.makeText(this, "Skanowanie zakończone", Toast.LENGTH_SHORT).show()
        }, 8000)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (!devices.contains(device)) {
                    devices.add(device)
                    adapter.add("${device.name ?: "Unknown"}\n${device.address}")
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}
