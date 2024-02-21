package com.github.litbay.embeddedtools.server.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

/*
*蓝牙初结构体*/
@RequiresApi(Build.VERSION_CODES.S)
class Bluetooth(private val context: Context, private val activity: Activity) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    val initSuccess = (bluetoothAdapter != null && isPermittedConnect() && isEnabled())
    init {
        if (!isPermittedConnect()) applyPermission()
        if (isPermittedConnect() && !isEnabled()) turnOnBluetooth()
    }

    /*
    * 检测蓝牙 permission.BLUETOOTH_CONNECT 权限*/
    private fun isPermittedConnect(): Boolean {
        val isPermittedConnect = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
        return isPermittedConnect == PackageManager.PERMISSION_GRANTED
    }
    /*
    * 请求蓝牙 permission.BLUETOOTH_CONNECT 权限*/
    private fun applyPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            1
        )
    }

    /*
    * 检测蓝牙开关*/
    private fun isEnabled(): Boolean {
        return when(bluetoothAdapter?.isEnabled) {
            false, null -> false
            true -> true
        }
    }

    /*
    * 开启蓝牙开关*/
    private fun turnOnBluetooth() {
        ActivityCompat.startActivityForResult(
            activity,
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
            1,
            null
        )
    }

    fun getPairedDevices(): Set<BluetoothDevice>? {
        var pairedDevices: Set<BluetoothDevice>? = null
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pairedDevices = bluetoothAdapter?.bondedDevices
        } else applyPermission()
        return pairedDevices
    }
}