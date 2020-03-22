package com.vspector.bluetoothvisor

interface MainView {
    fun checkBluetoothAvailable()
    fun checkBluetoothEnabled()
    fun registerBluetoothReceiver()
    fun startDevicesDiscovery()
    fun stopDevicesDiscovery()
    fun requestBluetooth()
    fun updateDevicesList(devices: List<Device>)
    fun showAlert(text: String, onDismiss: () -> Unit)
    fun finish()
    fun unregisterReceiver()
}