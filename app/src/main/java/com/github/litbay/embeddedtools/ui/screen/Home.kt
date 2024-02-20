package com.github.litbay.embeddedtools.ui.screen


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun HomeScreenNav(context: Context, navController: NavHostController) {
    Column(modifier = Modifier.safeContentPadding().fillMaxWidth()) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("blueTooth") {
                BluetoothScreen(context = context)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { navController.navigate("blueTooth") }) {
            Text(text = "蓝牙", fontSize = 20.sp)
        }
    }
}