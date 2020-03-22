package com.vspector.bluetoothvisor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(private val devices: List<Device>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DeviceViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bluetooth_device, parent, false) as TextView
        return DeviceViewHolder(textView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.textView.text = device.mac
    }

    override fun getItemCount() = devices.size
}