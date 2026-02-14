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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class ScanActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var bleScanner: BluetoothLeScanner

    private val devices = mutableListOf<android.bluetooth.BluetoothDevice>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btnScan = Button(this).apply { text = "Rozpocznij skanowanie" }
        listView = ListView(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(btnScan)
            addView(listView)
        }
        setContentView(layout)

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Urządzenie nie obsługuje Bluetooth!", Toast.LENGTH_LONG).show()
            return
        }

        bleScanner = bluetoothAdapter.bluetoothLeScanner

        btnScan.setOnClickListener {
            checkBluetoothAndPermissions()
        }
    }

    private fun checkBluetoothAndPermissions() {
        // Bluetooth off?
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
            return
        }

        // Missing permissions?
        val needed = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 2)
        } else {
            startBleScan()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2) {
            val denied = grantResults.any { it != PackageManager.PERMISSION_GRANTED }
            if (denied) {
                AlertDialog.Builder(this)
                    .setTitle("Uprawnienia wymagane")
                    .setMessage("Aplikacja potrzebuje uprawnień do Bluetooth i lokalizacji, aby skanować urządzenia.")
                    .setPositiveButton("Dalej") { _, _ -> checkBluetoothAndPermissions() }
                    .setNegativeButton("Anuluj", null)
                    .show()
            } else {
                startBleScan()
            }
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
}
