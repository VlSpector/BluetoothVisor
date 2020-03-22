package com.vspector.bluetoothvisor

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    private val presenter: MainPresenter by lazy {
        MainPresenter(view = this) // Should be injected
    }

    private lateinit var recyclerView: RecyclerView
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        getDefaultAdapter()
    }

    private var viewAdapter: DeviceAdapter = DeviceAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = recycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = viewAdapter

        presenter.onCreate()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun checkBluetoothAvailable() {
        if (bluetoothAdapter == null) {
            presenter.onBluetoothNotAvailable()
            return
        }

        presenter.onBluetoothAvailable()
    }

    override fun checkBluetoothEnabled() {
        // TODO: we must also request ACCESS_FINE_LOCATION for Android 10+
        if (bluetoothAdapter?.isEnabled == false) {
            presenter.onBluetoothDisabled()
            return
        }

        presenter.onBluetoothEnabled()
    }

    override fun registerBluetoothReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_FOUND)
            addAction(ACTION_DISCOVERY_STARTED)
            addAction(ACTION_DISCOVERY_FINISHED)
            addAction(ACTION_STATE_CHANGED)
        }

        registerReceiver(receiver, intentFilter)
    }

    override fun startDevicesDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDevicesDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun requestBluetooth() {
        val enableBtIntent = Intent(ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_CODE)
    }

    override fun updateDevicesList(devices: List<Device>) {
        viewAdapter = DeviceAdapter(devices)
        recyclerView.adapter = viewAdapter
    }

    override fun showAlert(text: String, onDismiss: () -> Unit) {
        AlertDialog.Builder(this@MainActivity).apply {
            setMessage(text)
            setNeutralButton("Got It") { _, _ -> onDismiss.invoke() }
            setCancelable(false)
        }.create().show()
    }

    override fun unregisterReceiver() {
        unregisterReceiver(receiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.onBluetoothEnabled()
            } else {
                presenter.onBluetoothDenied()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // We may also want cover case, when we are already scanning, but Bluetooth just been disabled

            when (intent.action) {
                ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceHardwareAddress = device.address

                    presenter.onDeviceFound(mac = deviceHardwareAddress)
                }
                ACTION_STATE_CHANGED -> presenter.onBluetoothEnabled()
                ACTION_DISCOVERY_STARTED -> presenter.onStartDiscovery()

                ACTION_DISCOVERY_FINISHED -> presenter.onFinishDiscovery()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 42
    }
}
