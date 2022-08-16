package minerofmillions.recipeapp

import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import minerofmillions.recipeapp.components.IApplication
import minerofmillions.recipeapp.components.defaults.ApplicationComponent
import minerofmillions.recipeapp.ui.ApplicationUI

@OptIn(ExperimentalDecomposeApi::class)
fun main() {
	val lifecycle = LifecycleRegistry()
	val application: IApplication = ApplicationComponent(DefaultComponentContext(lifecycle))
	
	application {
		val windowState = rememberWindowState()
		val icon = painterResource("Icon.png")
		
		LifecycleController(lifecycle, windowState)
		Window(state = windowState, onCloseRequest = ::exitApplication, icon = icon, title = "Recipe Calculator") {
			ApplicationUI(application)
		}
	}
}
