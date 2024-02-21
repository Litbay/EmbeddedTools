package com.github.litbay.embeddedtools.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SwitchLeft
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.litbay.embeddedtools.server.bluetooth.Bluetooth
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
    var topAppBarTitle by remember { mutableStateOf("") }
    var dropdownMenuExpended by remember { mutableStateOf(false) }

    Column {
        Column {
            BluetoothTopAppBar(topAppBarTitle, dropdownMenuExpended, navController) { dropdownMenuExpended = it }
        }
        NavHost(
            navController = navController,
            startDestination = "blueTooth.legacy",
            modifier = Modifier.fillMaxWidth()
        ) {
            composable("blueTooth.legacy") {
                topAppBarTitle = "经典蓝牙"
                LegacyBluetoothScreen(bluetooth = bluetooth)
            }
            composable("blueTooth.ble") {
                topAppBarTitle = "BLE蓝牙"
                BLEScreen(bluetooth = bluetooth)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun LegacyBluetoothScreen(bluetooth: Bluetooth) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val pairedDevices = bluetooth.getPairedDevices()
        Text(text = "已配对设备列表：")
        pairedDevices?.forEach { device ->
            Text(text = "${device.name}（${device.address}）")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BLEScreen(bluetooth: Bluetooth) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "BLE功能页面")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothTopAppBar(
    topAppBarTitle: String,
    dropdownMenuExpended: Boolean,
    navController: NavHostController,
    actionsCallBack: (Boolean) -> Unit) {
    TopAppBar(
        title = { Text(text = topAppBarTitle) },
        navigationIcon = {
            Icon(imageVector = Icons.Filled.BluetoothConnected,
                contentDescription = null,
                modifier = Modifier.padding(10.dp)) },
        actions = {
            IconButton(onClick = { actionsCallBack(true) }) {
                Icon(imageVector = Icons.Filled.ExpandMore, contentDescription = null) }
            BluetoothModeSwitchMenu(dropdownMenuExpended, navController) { actionsCallBack(false) }},
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
    )
}

@Composable
private fun BluetoothModeSwitchMenu(
    dropdownMenuExpended: Boolean,
    navController: NavHostController,
    hideMenuCallBack: () -> Unit
) {
    DropdownMenu(
        expanded = dropdownMenuExpended,
        onDismissRequest = { hideMenuCallBack() }
    ) {
        DropdownMenuItem(
            leadingIcon = { Icon(imageVector = Icons.Filled.SwitchRight, contentDescription = null) },
            text = { Text(text = "经典蓝牙") },
            onClick = {
                navController.navigate("blueTooth.legacy") {
                    popUpTo("blueTooth.legacy") { inclusive = true }
                    popUpTo("blueTooth.ble") { inclusive = true }
                }
                hideMenuCallBack()
            }
        )
        DropdownMenuItem(
            leadingIcon = { Icon(imageVector = Icons.Filled.SwitchLeft, contentDescription = null) },
            text = { Text(text = "BLE蓝牙") },
            onClick = {
                navController.navigate("blueTooth.ble") {
                    popUpTo("blueTooth.ble") { inclusive = true }
                    popUpTo("blueTooth.legacy") { inclusive = true }
                }
                hideMenuCallBack()
            }
        )
    }
}
