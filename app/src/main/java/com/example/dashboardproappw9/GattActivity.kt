package com.example.dashboardproappw9

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class GattActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var listView: ListView
    private lateinit var statusView: TextView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gatt)

        statusView = findViewById(R.id.tvGattStatus)
        listView = findViewById(R.id.listGatt)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        val mac = intent.getStringExtra("MAC")
        if (mac != null) {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
                return
            }

            statusView.text = "Connecting to BLE device..."
            val device = bluetoothAdapter.getRemoteDevice(mac)

            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            statusView.text = "Device MAC not found!"
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            runOnUiThread {
                if (newState == BluetoothAdapter.STATE_CONNECTED) {
                    statusView.text = "Connected! Discovering services..."
                    bluetoothGatt?.discoverServices()
                } else {
                    statusView.text = "Disconnected from device"
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            runOnUiThread {
                adapter.clear()
                adapter.add("=== Services & Characteristics ===")
            }

            for (service in gatt.services) {
                displayService(service)
            }
        }
    }

    private fun displayService(service: BluetoothGattService) {
        runOnUiThread {
            adapter.add("Service: ${service.uuid}")
        }

        for (characteristic in service.characteristics) {
            val props = getProperties(characteristic.properties)
            runOnUiThread {
                adapter.add("  Char: ${characteristic.uuid} ($props)")
            }
        }
    }

    private fun getProperties(props: Int): String {
        val list = mutableListOf<String>()
        if (props and BluetoothGattCharacteristic.PROPERTY_READ != 0) list.add("READ")
        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) list.add("WRITE")
        if (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) list.add("NOTIFY")
        if (props and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) list.add("WRITE_NO_RESP")
        return list.joinToString("|")
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
    }
}
