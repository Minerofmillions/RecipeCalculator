package minerofmillions.recipeapp.components.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.recipeapp.components.IItemList
import minerofmillions.recipeapp.entities.saver.Item

class ItemListComponent(context: ComponentContext, override val itemList: List<Item>, private val onItemSelected: (Item) -> Unit) : IItemList, ComponentContext by context {
	override fun onItemClicked(item: Item) = onItemSelected(item)
}
