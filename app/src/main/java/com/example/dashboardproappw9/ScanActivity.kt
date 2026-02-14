package com.example.dashboardproappw9

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class ScanActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bleScanner: BluetoothLeScanner

    private val devices = mutableListOf<BluetoothDevice>()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val btnScan = findViewById<Button>(R.id.btnScan)
        listView = findViewById(R.id.listDevices)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        listView.adapter = adapter

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            val enableBt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBt, 1)
        }

        bleScanner = bluetoothAdapter.bluetoothLeScanner

        btnScan.setOnClickListener {
            checkPermissionsAndScan()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = devices[position]
            connectToDevice(device)
        }
    }

    private fun checkPermissionsAndScan() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val missing = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1)
            return
        }
        startBleScan()
    }

    private fun startBleScan() {
        adapter.clear()
        devices.clear()

        bleScanner.startScan(scanCallback)
        Toast.makeText(this, "Skanowanie BLE...", Toast.LENGTH_SHORT).show()

        // stop after 10s
        listView.postDelayed({
            bleScanner.stopScan(scanCallback)
            Toast.makeText(this, "Skanowanie zakończone", Toast.LENGTH_SHORT).show()
        }, 10000)
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

    private fun connectToDevice(device: BluetoothDevice) {
        Toast.makeText(this, "Wybrano: ${device.name}", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DeviceActivity::class.java)
        intent.putExtra("MAC", device.address)
        startActivity(intent)
    }
}
