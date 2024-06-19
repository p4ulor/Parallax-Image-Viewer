package com.vluk4.parallaxview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vluk4.parallaxview.ui.screens.ExampleFullScreen2
import com.vluk4.parallaxview.ui.screens.ExampleFullScreen1
import com.vluk4.parallaxview.ui.screens.HomeScreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Home) {
                    composable<Home> { HomeScreen(navController) }
                    composable<ParallaxScreen1> { ExampleFullScreen1() }
                    composable<ParallaxScreen2> { ExampleFullScreen2() }
                }
            }
        }
    }
}

@Serializable
object Home

@Serializable
object ParallaxScreen2

@Serializable
object ParallaxScreen1