package minerofmillions.recipeapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import minerofmillions.recipeapp.components.IMainScreen

@Composable
fun MainScreen(mainScreen: IMainScreen) {
	Column {

		Row {
			Button(onClick = mainScreen::onShowItems) {
				Text("Show Items")
			}
			Button(onClick = mainScreen::onShowRecipes) {
				Text("Show Recipes")
			}
			Button(onClick = mainScreen::onShowNPCs) {
				Text("Show NPCs")
			}
			Button(onClick = mainScreen::onShowCalculator) {
				Text("Show Calculator")
			}
		}
	}
}
