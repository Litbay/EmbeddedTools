package com.github.litbay.embeddedtools

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.litbay.embeddedtools.ui.screen.BasicScreenNav
import com.github.litbay.embeddedtools.ui.theme.Background
import com.github.litbay.embeddedtools.ui.theme.EmbeddedToolsTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmbeddedToolsTheme() {
                Surface(modifier = Modifier.background(Background)) {
                    BasicScreenNav(applicationContext, this)
                }
            }
        }
    }
}