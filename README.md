# BluetoothVisor

The goal of this application is to scan and display nearby Bluetooth devices.
On sturtup, app requests Bluetooth to be enabled and start nearby devices discovery, when device is discovered, it's MAC address is added to list.

I've tryied to keep it as simple as possible, so no DI, state restoring on config changes, no localization and simpliest UI.
Real time spent: ~2.5h

Stack:
- Kotlin
- Mockito for unit-testing
- MVP as acrhitecture pattern
