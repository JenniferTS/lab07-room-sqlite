package com.example.mydatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mydatabase.tareas.ScreenTareas
import com.example.mydatabase.ui.theme.MyDatabaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDatabaseTheme {
                // 0 = usuarios (parte guiada y ejercicio 1), 1 = tareas (ejercicio 2)
                var pestana by rememberSaveable { mutableIntStateOf(0) }
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = pestana == 0,
                                onClick = { pestana = 0 },
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text("Usuarios") }
                            )
                            NavigationBarItem(
                                selected = pestana == 1,
                                onClick = { pestana = 1 },
                                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                label = { Text("Tareas") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                        if (pestana == 0) ScreenUser() else ScreenTareas()
                    }
                }
            }
        }
    }
}
