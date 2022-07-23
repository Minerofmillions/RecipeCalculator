package minerofmillions.recipeapp.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import minerofmillions.recipeapp.data.Recipe
import minerofmillions.recipeapp.data.recipeentities.TerrariaItem

@Composable
fun RecipeView(recipe: Recipe, modifier: Modifier = Modifier) {
	Column(modifier.padding(2.dp).border(1.dp, MaterialTheme.colors.primary).fillMaxWidth()) {
		Column(Modifier.padding(4.dp)) {
			Text(recipe.name)
			Text(recipe.inputs.joinToString { it.toString() }, style = MaterialTheme.typography.body2)
			Text(recipe.outputs.joinToString { it.toString() }, style = MaterialTheme.typography.body2)
		}
	}
}

@Composable
fun ItemView(item: TerrariaItem, modifier: Modifier = Modifier) {
	Column(modifier.padding(2.dp).border(1.dp, MaterialTheme.colors.primary).fillMaxWidth()) {
		Column(Modifier.padding(4.dp)) {
			Text(item.namespacedName, style = MaterialTheme.typography.h1)
			Text(item.type.toString(), style = MaterialTheme.typography.h2)
			Text(item.value.toString())
			Column(Modifier.padding(horizontal = 4.dp, vertical = 8.dp).border(1.dp, MaterialTheme.colors.primary).width(IntrinsicSize.Min)) {
				item.bagItems.getNormalDrops().forEach { (item, amount) ->
					Row(Modifier.padding(horizontal = 4.dp)) {
						Text(item.toString(), Modifier.weight(1f), style = MaterialTheme.typography.body1)
						Text(amount.toString(5), Modifier.padding(start = 8.dp).align(Alignment.CenterVertically), style = MaterialTheme.typography.body2)
					}
				}
			}
		}
	}
}
