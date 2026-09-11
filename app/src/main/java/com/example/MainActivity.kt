package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Project
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EditorViewModel

enum class AppScreen {
    HOME,
    EDITOR
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBg
                ) {
                    KlipzApp()
                }
            }
        }
    }
}

@Composable
fun KlipzApp(editorViewModel: EditorViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(
                viewModel = editorViewModel,
                onOpenProject = { project: Project ->
                    editorViewModel.loadProject(project)
                    currentScreen = AppScreen.EDITOR
                }
            )
        }
        AppScreen.EDITOR -> {
            EditorScreen(
                viewModel = editorViewModel,
                onNavigateBack = {
                    currentScreen = AppScreen.HOME
                }
            )
        }
    }
}
