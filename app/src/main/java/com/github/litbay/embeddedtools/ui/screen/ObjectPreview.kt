package com.github.litbay.embeddedtools.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.litbay.embeddedtools.ui.theme.EmbeddedToolsTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    EmbeddedToolsTheme {
        //
    }
}


/**
 * 蓝牙页面预览
 */
@Preview(showSystemUi = true)
@Composable
fun BluetoothPagePreview(){
    EmbeddedToolsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            //BluetoothPage()
        }
    }
}
