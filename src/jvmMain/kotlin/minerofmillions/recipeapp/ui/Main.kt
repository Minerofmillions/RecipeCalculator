package minerofmillions.recipeapp.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.ResourceLoader
//import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

@Composable
@Preview
fun App() {
	var darkMode by remember { mutableStateOf(true) }
	val density = LocalDensity.current
	MaterialTheme(colors = if (darkMode) darkColors() else lightColors()) {
		Scaffold(topBar = {
			TopAppBar(
				actions = {
					AppActions()
					IconButton(onClick = { darkMode = !darkMode }) {
//						Icon(Icons.Default.Favorite, if (darkMode) "Dark Mode" else "Light Mode")
						if (darkMode) AsyncImage(
							load = { loadSvgPainter(javaClass.getResource("/light_mode_white.svg"), density) },
							painterFor = { it },
							contentDescription = "Light Mode",
						)
						else AsyncImage(
							load = { loadSvgPainter(javaClass.getResource("/dark_mode_white.svg"), density) },
							painterFor = { it },
							contentDescription = "Dark Mode",
						)
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
