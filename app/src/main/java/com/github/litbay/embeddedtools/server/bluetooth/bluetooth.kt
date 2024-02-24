package com.github.litbay.embeddedtools.server.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat

/*
*蓝牙初结构体*/
@RequiresApi(Build.VERSION_CODES.S)
class Bluetooth(private val context: Context, private val activity: Activity) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    val initSuccess = (bluetoothAdapter != null && isPermittedConnect() && isEnabled())
    var isScanning = mutableStateOf(false)

    @SuppressLint("MutableCollectionMutableState")
    val nearDevices = mutableStateOf(mutableSetOf<BluetoothDevice?>())

    private val btBroadcastReceiver = BtBroadcastReceiver {
        cmd: String, device: BluetoothDevice? ->
        when(cmd) {
            "discoveryFinished" -> {
                isScanning.value = false
                unregisterReceiver()
            }
            "foundDevice" -> {
                nearDevices.value.add(device)
            }
        }
    }

    init {
        if (!isPermittedConnect()) applyPermission(Manifest.permission.BLUETOOTH_CONNECT)
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
    private fun applyPermission(permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
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

    /*
    * 注册蓝牙广播接收器*/
    private fun registerBroadcastReceiver() {
        val btFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(btBroadcastReceiver, btFilter)
    }

    /*
    * 注销蓝牙广播接收器*/
    private fun unregisterReceiver() {
        context.unregisterReceiver(btBroadcastReceiver)
    }

    /*
    * 开启蓝牙扫描*/
    fun startDiscovery() {
        if (isScanning.value || !isEnabled()) return
        isScanning.value = true
        nearDevices.value.clear()
        registerBroadcastReceiver()
        /*
        * 鉴权并开启扫描*/
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter?.startDiscovery()
            } else {
                isScanning.value = false
                unregisterReceiver()
                applyPermission(Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            isScanning.value = false
            unregisterReceiver()
            applyPermission(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }

    /*
    * 启用可检测性*/
    fun makeDiscoverable() {
        val requestCode = 1
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 90) //90秒内可见
        }
        ActivityCompat.startActivityForResult(
            activity,
            discoverableIntent,
            requestCode,
            null
        )
    }

    /*
    * 获取已配对设备列表*/
    fun getPairedDevices(): Set<BluetoothDevice>? {
        var pairedDevices: Set<BluetoothDevice>? = null
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pairedDevices = bluetoothAdapter?.bondedDevices
        } else applyPermission(Manifest.permission.BLUETOOTH_SCAN)
        return pairedDevices
    }

    /*解码BluetoothDevice类，避免外部解码权限报错*/
    fun decodeDevice(bluetoothDevice: BluetoothDevice): DecodedBluetoothDevice {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            DecodedBluetoothDevice(bluetoothDevice.name, bluetoothDevice.address)
        } else {
            applyPermission(Manifest.permission.BLUETOOTH_CONNECT)
            DecodedBluetoothDevice("Error", "0::0")
        }
    }
}

class BtBroadcastReceiver(val callback: (String, BluetoothDevice?) -> Unit): BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            /*
            * 发现新蓝牙设备*/
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(
                            BluetoothDevice.EXTRA_DEVICE,
                            BluetoothDevice::class.java
                        )
                callback("foundDevice", device)
            }
            /*
            * 蓝牙扫描结束*/
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> { callback("discoveryFinished", null) }
        }
    }
}

data class DecodedBluetoothDevice(val name: String, val address: String)