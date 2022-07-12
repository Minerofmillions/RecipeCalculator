package minerofmillions.recipeapp.state

import androidx.compose.runtime.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import minerofmillions.recipeapp.data.ItemStack
import minerofmillions.recipeapp.data.Rational
import minerofmillions.recipeapp.data.Recipe
import minerofmillions.recipeapp.data.recipeentities.TerrariaEnemy
import minerofmillions.recipeapp.data.recipeentities.TerrariaItem
import minerofmillions.recipeapp.data.recipeentities.TerrariaItemStack
import minerofmillions.recipeapp.data.recipeentities.TerrariaRecipe
import minerofmillions.recipeapp.data.times
import minerofmillions.recipeapp.util.resolve
import java.io.File
import javax.swing.filechooser.FileSystemView

class CalculatorState internal constructor(private val scope: CoroutineScope) {
	var loadingRecipes by mutableStateOf(false)
		private set
	
	private val _recipes = mutableStateListOf<Recipe>()
	private val _products = mutableStateListOf<ItemStack>()
	
	private var loadJob: Job? = null
	
	val recipes: List<Recipe> = _recipes
	val products: List<ItemStack> = _products
	
	fun loadRecipes() {
		loadJob = scope.launch(Dispatchers.IO) {
			loadingRecipes = true
			_recipes.clear()
			_products.clear()
			
			val saverDir = FileSystemView.getFileSystemView().defaultDirectory.resolve(
				"My Games", "Terraria", "tModLoader", "Saver"
			)
			val items = File(saverDir, "Items.json").takeIf(File::exists)?.reader()?.use { reader ->
				gson.fromJson<Set<TerrariaItem>>(reader, object : TypeToken<Set<TerrariaItem>>() {}.type).filterNot { it.type == 0 }.sorted()
			} ?: error("Could not find Items.json")
			val enemies = File(saverDir, "Enemies.json").takeIf(File::exists)?.reader()?.use { reader ->
				gson.fromJson<Set<TerrariaEnemy>>(reader, object : TypeToken<Set<TerrariaEnemy>>() {}.type).filterNot { it.type == 0 }.sorted()
			} ?: error("Could not find Enemies.json")
			val recipes = File(saverDir, "Recipes.json").takeIf(File::exists)?.reader()?.use {
				gson.fromJson<List<TerrariaRecipe>>(it, object : TypeToken<List<TerrariaRecipe>>() {}.type).sorted()
			} ?: error("Could not find Recipes.json")
			
			val itemsByName = items.groupBy(TerrariaItem::name)
			val allRecipes = if (itemsByName.any { it.value.count() > 1 }) generateIdRecipes(
				items, enemies, recipes
			)
			else generateNameRecipes(items, enemies, recipes)
			
			_recipes.addAll(allRecipes)
		}.apply { invokeOnCompletion { loadingRecipes = false } }
	}
	
	private fun generateIdRecipes(
		items: List<TerrariaItem>,
		enemies: List<TerrariaEnemy>,
		recipes: List<TerrariaRecipe>,
	) = generateRecipes(items, enemies, recipes) {
		it.type.toString()
	}
	
	private fun generateNameRecipes(
		items: List<TerrariaItem>,
		enemies: List<TerrariaEnemy>,
		recipes: List<TerrariaRecipe>,
	) = generateRecipes(items, enemies, recipes, TerrariaItem::name)
	
	private fun generateRecipes(
		items: List<TerrariaItem>,
		enemies: List<TerrariaEnemy>,
		recipes: List<TerrariaRecipe>,
		stackItemSelector: (TerrariaItem) -> String,
	): Sequence<Recipe> = sequence {
		for (item in items) {
			if (item.type == 0 || item.type in 71..74) continue
			if (item.value > 0) {
				yield(
					Recipe(
						"Sell ${item.name}",
						listOf(stackItemSelector(item) * 1),
						generateCoins(item.value / 5,
							(71..74).map { id -> stackItemSelector(items.first { it.type == id }) })
					)
				)
			}
		}
		for (enemy in enemies) {
			if (enemy.type == 0) continue
			if (enemy.masterDrops.isNotEmpty()) yield(Recipe("Kill (M) ${enemy.name}",
				emptyList(),
				items.map { stackItemSelector(it) * enemy.masterDrops.getValue(it.type) }.filter { it.amount > 0 })
			)
			if (enemy.expertDrops.isNotEmpty()) yield(Recipe("Kill (E) ${enemy.name}",
				emptyList(),
				items.map { stackItemSelector(it) * enemy.expertDrops.getValue(it.type) }.filter { it.amount > 0 })
			)
			if (enemy.normalDrops.isNotEmpty() || enemy.globalDrops.isNotEmpty()) yield(Recipe("Kill ${enemy.name}",
				emptyList(),
				items.map { stackItemSelector(it) * enemy.normalDrops.getValue(it.type) }.filter { it.amount > 0 })
			)
		}
		
		for (recipe in recipes) {
			val requiredItems = recipe.requiredItems.map { item -> stackItemSelector(items.first { it.type == item.item }) * item.amount }
			val createItem = stackItemSelector(items.first { it.type == recipe.createItem.item }) * recipe.createItem.amount
			yield(Recipe("${recipe.requiredTiles} ${recipe.createItem}", requiredItems, listOf(createItem)))
		}
	}
	
	private fun generateCoins(value: Int, coinNames: List<String>): List<ItemStack> =
		listOfNotNull((coinNames[3] * (value / 1000000)).takeIf { it.amount > 0 },
			(coinNames[2] * ((value / 10000) % 100)).takeIf { it.amount > 0 },
			(coinNames[1] * ((value / 100) % 100)).takeIf { it.amount > 0 },
			(coinNames[0] * (value % 100)).takeIf { it.amount > 0 })
	
	fun clearRecipes() {
		_recipes.clear()
		_products.clear()
		
		loadJob?.cancel()
		loadJob = null
	}
	
	companion object {
		private val gson: Gson = GsonBuilder().registerTypeAdapter(ItemStack::class.java, ItemStack.Serializer)
			.registerTypeAdapter(Rational::class.java, Rational.Serializer)
			.registerTypeAdapter(TerrariaEnemy::class.java, TerrariaEnemy.Serializer)
			.registerTypeAdapter(TerrariaItemStack::class.java, TerrariaItemStack.Serializer).create()
	}
}

@Composable
fun rememberCalculatorState(scope: CoroutineScope = rememberCoroutineScope()) = remember { CalculatorState(scope) }
