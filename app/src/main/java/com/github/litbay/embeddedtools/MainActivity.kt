package com.github.litbay.embeddedtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.github.litbay.embeddedtools.ui.screen.HomeScreenNav
import com.github.litbay.embeddedtools.ui.theme.EmbeddedToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmbeddedToolsTheme {
                val navController = rememberNavController()
                HomeScreenNav(context = applicationContext, navController = navController)
            }
        }
    }
}