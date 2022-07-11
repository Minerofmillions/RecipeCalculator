package minerofmillions.recipeapp.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
	var darkMode by remember { mutableStateOf(true) }
	MaterialTheme(colors = if (darkMode) darkColors() else lightColors()) {
		Scaffold(topBar = {
			TopAppBar(
				actions = {
					AppActions()
					IconToggleButton(checked = darkMode, onCheckedChange = { darkMode = !darkMode }) {
						Icon(Icons.Default.Favorite, if (darkMode) "Dark Mode" else "Light Mode")
					}
				},
				title = {
					Text("Recipe Calculator")
				}
			)
		}) { padding ->
			Column(Modifier.padding(padding)) {
			
			}
		}
	}
}

@Composable
fun AppActions() {

}

fun main() = application {
	Window(onCloseRequest = ::exitApplication) {
		App()
	}
}
