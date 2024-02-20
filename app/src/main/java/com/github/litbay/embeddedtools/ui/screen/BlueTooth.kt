package com.github.litbay.embeddedtools.ui.screen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.litbay.embeddedtools.ui.component.FullErrorPage

//定义蓝牙初始化信息结构体
object BlueToothInitInfo {
    lateinit var bluetoothManager: BluetoothManager
    var bluetoothAdapter: BluetoothAdapter? = null
    //val isEnable = mutableStateOf(this.bluetoothAdapter?.isEnabled)
}

@Composable
fun BluetoothScreen(context: Context){

    //蓝牙初始化
    if (!initBlueTooth(context)) {
        FullErrorPage(text = "您的设备不支持蓝牙")
        return
    }

    var bluetoothEnable by remember { mutableStateOf(BlueToothInitInfo.bluetoothAdapter?.isEnabled) }

    Column(
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (bluetoothEnable !=true) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "蓝牙当前已关闭，请打开蓝牙后刷新")
                Spacer(modifier = Modifier.size(10.dp))
                Button(onClick = { bluetoothEnable = BlueToothInitInfo.bluetoothAdapter?.isEnabled }) {
                    Text(text = "刷新")
                }
            }
            return
        }

        Text(text = "蓝牙调试器Demo", fontSize = 30.sp, modifier = Modifier.padding(20.dp))
        /*     val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
             Text(text = "已配对设备列表：")
             if (pairedDevices != null) {
                 for (device in pairedDevices) {
                     Text(text = "${device.name}（${device.address}）")
                 }
             }*/
    }
}


fun initBlueTooth(context: Context): Boolean {
    BlueToothInitInfo.bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    BlueToothInitInfo.bluetoothAdapter = BlueToothInitInfo.bluetoothManager.adapter
    return BlueToothInitInfo.bluetoothAdapter != null
}