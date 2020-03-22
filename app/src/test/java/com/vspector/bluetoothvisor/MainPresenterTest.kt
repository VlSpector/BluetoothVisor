package com.vspector.bluetoothvisor

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test


class MainPresenterTest {

    private val view = mock<MainView>{}
    private val presenter = MainPresenter(view)

    @Test
    fun `register bluetooth receiver onCreate`() {
        presenter.onCreate()

        verify(view).registerBluetoothReceiver()
    }

    @Test
    fun `BT availability checked onStart`() {
        presenter.onStart()

        verify(view).checkBluetoothAvailable()
    }

    @Test
    fun `request BT if it's disabled`() {
        presenter.onBluetoothDisabled()

        verify(view).requestBluetooth()
    }

    @Test
    fun `add new-found device to list and view updated`() {
        val mac = "mac"
        presenter.onDeviceFound(mac)

        verify(view).updateDevicesList(listOf(Device(mac = mac)))
    }

    @Test
    fun `rediscover existing device set it as discovered`() {
        val presenter = MainPresenter(
            view, mutableListOf(Device(mac = "mac", isDiscovered = false))
        )
        val mac = "mac"

        presenter.onDeviceFound(mac)

        verify(view).updateDevicesList(listOf(Device(mac = "mac", isDiscovered = true)))
    }

    @Test
    fun `remove undiscovered devices on finish, pass list to view and start new discovery`() {
        val presenter = MainPresenter(
            view,
            mutableListOf(
                Device(mac = "mac1", isDiscovered = false),
                Device(mac = "mac2", isDiscovered = true)
            )
        )
        presenter.onFinishDiscovery()

        verify(view).updateDevicesList(listOf(Device(mac = "mac2", isDiscovered = true)))
        verify(view).startDevicesDiscovery()
    }

    @Test
    fun `show alert on BT not available`() {
        presenter.onBluetoothNotAvailable()

        // TODO: we may want to move Alert button action to presenter method call instead of passing it directly from presenter to make it testable.
        verify(view).showAlert(
            eq("Sorry, your device doesn't support Bluetooth discovery =("),
            any()
        )
    }

    @Test
    fun `show alert on BT enable denied`() {
        presenter.onBluetoothDenied()

        // TODO: we may want to move Alert button action to presenter method call instead of passing it directly from presenter to make it testable.
        verify(view).showAlert(
            eq("Sorry, the app cannot work without Bluetooth enabled"),
            any()
        )
    }

    @Test
    fun `check BT enabled if it is available`() {
        presenter.onBluetoothAvailable()

        verify(view).checkBluetoothEnabled()
    }

    @Test
    fun `start discovery on BT enabled`() {
        presenter.onBluetoothEnabled()

        verify(view).startDevicesDiscovery()
    }
}
