package minerofmillions.recipeapp.data.recipeentities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import minerofmillions.recipeapp.data.ItemStack
import minerofmillions.recipeapp.data.Rational
import java.lang.reflect.Type

data class TerrariaItemStack(val item: Int, val amount: Rational) : Comparable<TerrariaItemStack> {
	override infix fun compareTo(other: TerrariaItemStack): Int = comparator.compare(this, other)
	override fun toString(): String {
		return "$amount * $item"
	}
	
	object Serializer : JsonDeserializer<TerrariaItemStack> {
		override fun deserialize(
			json: JsonElement,
			typeOfT: Type,
			context: JsonDeserializationContext,
		): TerrariaItemStack = json.asJsonObject.let { obj ->
			TerrariaItemStack((obj["id"] ?: obj["type"]).asInt, Rational.parse(obj["stack"].asString))
		}
	}
	
	companion object {
		private val comparator = compareBy(TerrariaItemStack::item, TerrariaItemStack::amount)
	}
}
