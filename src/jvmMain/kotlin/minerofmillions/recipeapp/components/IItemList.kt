package minerofmillions.recipeapp.components

import minerofmillions.recipeapp.entities.saver.Item

interface IItemList {
	val itemList: List<Item>
	fun onItemClicked(item: Item)
	fun onBackClicked()
}
