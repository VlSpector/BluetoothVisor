package com.vspector.bluetoothvisor

class MainPresenter(
    private val view: MainView,
    private val devices: MutableList<Device> = mutableListOf()
) {
    fun onCreate() = view.registerBluetoothReceiver()

    fun onStart() = view.checkBluetoothAvailable()

    fun onStop() = view.stopDevicesDiscovery()

    fun onBluetoothDisabled() = view.requestBluetooth()

    fun onDeviceFound(mac: String) {
        val device = devices.firstOrNull { it.mac == mac }?.apply { isDiscovered = true }
        if (device == null) {
            devices.add(Device(mac = mac))
        }

        view.updateDevicesList(devices)
    }

    fun onStartDiscovery() {
        devices.map { it.isDiscovered = false }
    }

    fun onFinishDiscovery() {
        // Remove previously discovered devices since we didn't discover them at this iteration
        devices.removeAll { !it.isDiscovered }
        view.updateDevicesList(devices)

        // Instant discovery restart may lead to weired behavior so better have at least a delay here
        view.startDevicesDiscovery()
    }

    fun onBluetoothNotAvailable() {
        view.showAlert(
            "Sorry, your device doesn't support Bluetooth discovery =("
        ) { view.finish() }
    }

    fun onBluetoothAvailable() = view.checkBluetoothEnabled()

    fun onBluetoothEnabled() = view.startDevicesDiscovery()

    fun onBluetoothDenied() {
        view.showAlert(
            "Sorry, the app cannot work without Bluetooth enabled"
        ) { view.finish() }
    }

    fun onDestroy() {
        view.unregisterReceiver()
    }
}