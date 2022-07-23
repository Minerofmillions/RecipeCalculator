package minerofmillions.recipeapp.data.recipeentities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import minerofmillions.recipeapp.data.Rational
import java.lang.reflect.Type

class TerrariaItem(val type: Int, val name: String, val mod: String, val value: Int, val createTile: Int?, val createWall: Int?, val bagItems: TerrariaLoot, extractinatorItems: Map<Int, Int>) : Comparable<TerrariaItem> {
	val extractinatorItems = extractinatorItems.mapValues { (_, count) -> Rational.of(count, 10000) }
	val namespacedName get() = "${mod}:${name}"
	
	override fun compareTo(other: TerrariaItem) = comparator.compare(this, other)
	override fun toString() = "$name ($mod, $type) ($value, $createTile, $createWall, $bagItems)"
	companion object {
		val comparator = compareBy(TerrariaItem::type, TerrariaItem::name)
	}
	
	private object ExtractinatorItemsTypeToken : TypeToken<Map<Int, Int>>()
	object Serializer : JsonDeserializer<TerrariaItem> {
		override fun deserialize(
			json: JsonElement,
			typeOfT: Type,
			context: JsonDeserializationContext
		): TerrariaItem = json.asJsonObject.let { obj ->
			val type = obj["type"].asInt
			val name = obj["name"].asString
			val mod = obj["mod"].asString
			val value = obj["value"].asInt
			val createTile = obj["createTile"].let { if (it.isJsonNull) null else it.asInt }
			val createWall = obj["createWall"].let { if (it.isJsonNull) null else it.asInt }
			val bagItems = context.deserialize<TerrariaLoot>(obj["bagItems"], TerrariaLoot::class.java)
			val extractinatorItems = context.deserialize<Map<Int, Int>>(obj["extractinatorItems"], ExtractinatorItemsTypeToken.type)
			
			TerrariaItem(type, name, mod, value, createTile, createWall, bagItems, extractinatorItems)
		}
	}
}
