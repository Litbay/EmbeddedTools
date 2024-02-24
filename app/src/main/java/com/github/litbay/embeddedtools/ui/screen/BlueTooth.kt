package com.github.litbay.embeddedtools.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.litbay.embeddedtools.server.bluetooth.Bluetooth
import com.github.litbay.embeddedtools.server.bluetooth.DecodedBluetoothDevice
import com.github.litbay.embeddedtools.ui.theme.Background

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothScreenNav(context: Context, activity: Activity, backToHome: () -> Unit) {
    /*
    * 蓝牙初始化*/
    val bluetooth = Bluetooth(context, activity)
    if (!bluetooth.initSuccess) {
        backToHome()
        return
    }

    val navController = rememberNavController()
    val switchBtDestination = {
        forward: String, upTo: String, isInclusive: Boolean ->
        navController.navigate(forward) {popUpTo(upTo){inclusive = isInclusive} }
    }

    NavHost(
        navController = navController,
        startDestination = "bluetooth.home",
        modifier = Modifier.fillMaxWidth()
    ) {
        composable("bluetooth.home") {
            BtModeSelectScreen { switchBtDestination(it, "bluetooth.home", false) }
        }
        composable("bluetooth.legacy") {
            LegacyBtScreen(bluetooth) { switchBtDestination("bluetooth.home", "bluetooth.home", true) }
        }
        composable("bluetooth.ble") {
            BLEScreen(bluetooth) { switchBtDestination("bluetooth.home", "bluetooth.home", true) }
        }
    }
}


/*
* ---------------------以下为Screen----------------------------
* -----------------区分不同功能的蓝牙界面------------------------*/
/*
* 蓝牙模式选择界面*/
@Composable
fun BtModeSelectScreen(switchDestination: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
        Text(text = "传统蓝牙", fontSize = 18.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(64.dp)
                .clickable { switchDestination("bluetooth.legacy") })
        Text(text = "BLE蓝牙", fontSize = 18.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(64.dp)
                .clickable { switchDestination("bluetooth.ble") })
    }
}

/*
* 经典蓝牙功能界面*/
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun LegacyBtScreen(bluetooth: Bluetooth, backToHome: () -> Unit) {
    var showDeviceSelectDialog by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    var selectedDevice: DecodedBluetoothDevice? by remember { mutableStateOf(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothTopAppBar(topAppBarTitle = "传统蓝牙") {backToHome()}
        DeviceCard(
            deviceName = selectedDevice?.name,
            deviceAddress = selectedDevice?.address,
            isConnected = isConnected
        ) {
            bluetooth.startDiscovery()
            showDeviceSelectDialog = true
        }



        if (showDeviceSelectDialog) {
            Dialog(onDismissRequest = {  }) {
                Card {
                    Column {
                        if (bluetooth.isScanning.value) {
                            Text(text = "正在扫描附近设备")
                        } else {
                            bluetooth.nearDevices.value.forEach {
                                if (it != null) {
                                    val currentDevice = bluetooth.decodeDevice(it)
                                    Text(text = "${currentDevice.name}（${it.address}）",
                                        modifier = Modifier.clickable {
                                            showDeviceSelectDialog = false
                                            selectedDevice = currentDevice })
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(onClick = { isConnected = true }, enabled = !isConnected) {
            if (!isConnected) Text(text = "连接设备") else Text(text = "已连接")
        }

        Button(onClick = { isConnected = false }, enabled = isConnected) {
            Text(text = "断开设备连接")
        }

        Button(onClick = { bluetooth.startDiscovery() }, enabled = !bluetooth.isScanning.value) {
            if (!bluetooth.isScanning.value) Text(text = "开始扫描") else Text(text = "扫描中")
        }

        Card(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .weight(1f)) {
            Row(modifier = Modifier.padding(15.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(text = "附近设备：")
                        bluetooth.nearDevices.value.forEach {
                            if (it != null) {
                                val currentDevice = bluetooth.decodeDevice(it)
                                Text(text = "${currentDevice.name}（${it.address}）")
                            }
                        }
                    }
                }
            }
        }

        Row {
            Spacer(modifier = Modifier.size(15.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        }
        /*val pairedDevices = bluetooth.getPairedDevices()
        Text(text = "已配对设备列表：")
        pairedDevices?.forEach { device ->
            Text(text = "${device.name}（${device.address}）")
        }


        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "附近蓝牙设备列表：")
        Button(onClick = { bluetooth.startDiscovery() }) {
            Text(text = "开始扫描")
        }
        Text(text = "是否在扫描：${bluetooth.isScanning.value}")
        bluetooth.nearDevices.value.forEach { device ->
            if (device != null) {
                Text(text = "${device.name}（${device.address}）")
            }
        }

        Button(onClick = { bluetooth.makeDiscoverable() }) {
            Text(text = "启用可检测性")
        }*/
    }
}

/*
* BLE蓝牙功能界面*/
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BLEScreen(bluetooth: Bluetooth,backToHome: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothTopAppBar(topAppBarTitle = "BLE蓝牙") { backToHome() }
        Text(text = "BLE功能页面")
        Text(text = "（害妹写）")
        Card(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .weight(1f)) {
            Row(modifier = Modifier.padding(15.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(text = "附近设备：")
                        bluetooth.nearDevices.value.forEach {
                            if (it != null) {
                                val currentDevice = bluetooth.decodeDevice(it)
                                Text(text = "${currentDevice.name}（${it.address}）")
                            }
                        }
                    }
                }
            }
        }
    }
}


/*
* --------------------以下为蓝牙页面的组成Composable模块--------------------------
* ---------------------------------------------------------------------------*/

/*
* 蓝牙主页面的TopAppBar*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothTopAppBar(
    topAppBarTitle: String,
    backToHome: () -> Unit) {
    TopAppBar(
        title = { Text(text = topAppBarTitle) },
        navigationIcon = {
            IconButton(onClick = {backToHome()}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null)
            } },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
    )
}


/*
* 当前选中设备卡片*/
@Composable
fun DeviceCard(
    deviceName: String?,
    deviceAddress: String?,
    isConnected: Boolean,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Card(modifier = Modifier
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(5.dp))
            .clickable { onClick() },
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector =
                    if (deviceAddress == null) {
                        Icons.Filled.BluetoothDisabled
                    } else if(!isConnected) {
                        Icons.Filled.Bluetooth
                    } else { Icons.Filled.BluetoothConnected },
                    contentDescription = null,
                    Modifier.size(36.dp)
                )
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = when {
                        deviceName != null -> deviceName
                        deviceAddress != null -> "未知设备"
                        else -> "无设备"
                                     },
                        fontWeight = FontWeight.Bold)
                    Text(text = deviceAddress?:"点击此搜索附近设备", fontSize = 12.sp, modifier = Modifier.padding(3.dp))
                }
            }
        }
    }
}