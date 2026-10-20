package com.samuelampadu.wikireader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.samuelampadu.wikireader.ui.navigation.WikiNavHost
import com.samuelampadu.wikireader.ui.theme.WikiReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WikiReaderTheme {
                WikiNavHost()
            }
        }
    }
}
