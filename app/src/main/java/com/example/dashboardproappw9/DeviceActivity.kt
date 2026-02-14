package com.example.dashboardproappw9

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.util.*

class DeviceActivity : AppCompatActivity() {

    private var socket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        val statusView = findViewById<TextView>(R.id.tvStatus)
        val batteryView = findViewById<TextView>(R.id.tvBateria)

        val mac = intent.getStringExtra("MAC")
        if (mac != null) {
            statusView.text = "Łączenie..."
            val adapter = BluetoothAdapter.getDefaultAdapter()
            val device: BluetoothDevice = adapter.getRemoteDevice(mac)
            val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

            Thread {
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid)
                    socket!!.connect()

                    runOnUiThread {
                        statusView.text = "Połączono z ${device.name}"
                    }

                    val input: InputStream = socket!!.inputStream
                    val buffer = ByteArray(1024)
                    val bytes = input.read(buffer)
                    val data = String(buffer, 0, bytes)

                    runOnUiThread {
                        batteryView.text = "Odczyt: $data"
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        statusView.text = "Błąd połączenia"
                    }
                }
            }.start()
        }
    }
}
