package com.example.dashboardproappw9

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class GattActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gatt)

        val statusView = findViewById<TextView>(R.id.tvGattStatus)
        listView = findViewById(R.id.listGatt)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        val mac = intent.getStringExtra("MAC")
        if (mac != null) {
            val bluetoothAdapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter

            val device = bluetoothAdapter.getRemoteDevice(mac)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // request if needed
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
                return
            }

            statusView.text = "Connecting..."

            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        }
    }

    // GATT Callback
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            runOnUiThread {
                Log.d("BLE", "State changed: $status $newState")
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread {
                    Toast.makeText(this@GattActivity, "Connected GATT", Toast.LENGTH_SHORT).show()
                }
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread {
                    Toast.makeText(this@GattActivity, "Disconnected GATT", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            runOnUiThread {
                adapter.clear()
                adapter.add("=== Services Discovered ===")
            }

            bluetoothGatt?.services?.forEach { svc ->
                runOnUiThread {
                    adapter.add("Service: ${svc.uuid}")
                }

                svc.characteristics.forEach { chr ->
                    runOnUiThread {
                        adapter.add("  Characteristic: ${chr.uuid}  (${getProperties(chr.properties)})")
                    }
                }
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
