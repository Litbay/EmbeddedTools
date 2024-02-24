package com.github.litbay.embeddedtools.ui.screen

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BasicScreenNav(context: Context, activity: Activity) {
    val navController = rememberNavController()

    Column(modifier = Modifier
        .safeContentPadding()
        .fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            /*主页面导航*/
            composable("home") {
                HomeScreen(navController = navController)
            }
            /*蓝牙调试页面导航*/
            composable("blueTooth") {
                BluetoothScreenNav(context = context, activity = activity){
                    navController.navigate("home"){ popUpTo("home"){ inclusive = true } }
                }
            }
        }
    }
}

@Composable /*主屏幕：选择功能*/
fun HomeScreen(navController: NavHostController) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = { navController.navigate("blueTooth") }
            ) {
                Icon(imageVector = Icons.Filled.Bluetooth, modifier = Modifier.size(50.dp), contentDescription = null)
            }
            Text(text = "蓝牙", fontSize = 12.sp)
        }
    }
}