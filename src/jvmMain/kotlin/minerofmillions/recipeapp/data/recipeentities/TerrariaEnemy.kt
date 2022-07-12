package minerofmillions.recipeapp.data.recipeentities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import minerofmillions.recipeapp.data.Rational
import minerofmillions.recipeapp.data.recipeentities.TerrariaItem.Companion.comparator
import java.lang.reflect.Type

class TerrariaEnemy(
	val name: String,
	val type: Int,
	globalDrops: Map<Int, Rational>,
	normalDrops: Map<Int, Rational>,
	expertDrops: Map<Int, Rational>,
	masterDrops: Map<Int, Rational>,
) : Comparable<TerrariaEnemy> {
	val globalDrops: Map<Int, Rational> = globalDrops.withDefault { Rational.ZERO }
	val normalDrops: Map<Int, Rational> = normalDrops.withDefault { this.globalDrops.getValue(it) }
	val expertDrops: Map<Int, Rational> = expertDrops.withDefault { this.normalDrops.getValue(it) }
	val masterDrops: Map<Int, Rational> = masterDrops.withDefault { this.expertDrops.getValue(it) }
	
	override fun toString(): String =
		"\"$name\": g=$globalDrops, n=$normalDrops, e=$expertDrops, m=$masterDrops"
	override fun compareTo(other: TerrariaEnemy): Int = comparator.compare(this, other)
	
	private object DropsTypeToken : TypeToken<Map<Int, Rational>>()
	object Serializer : JsonDeserializer<TerrariaEnemy> {
		override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TerrariaEnemy =
			json.asJsonObject.let {
				val name = it["name"].asString
				val type = it["type"].asInt
				val globalDrops = context.deserialize<Map<Int, Rational>>(it["globalDrops"], DropsTypeToken.type)!!
				val normalDrops =
					context.deserialize<Map<Int, Rational>>(it["normalDrops"], DropsTypeToken.type) ?: emptyMap()
				val expertDrops =
					context.deserialize<Map<Int, Rational>>(it["expertDrops"], DropsTypeToken.type) ?: emptyMap()
				val masterDrops =
					context.deserialize<Map<Int, Rational>>(it["masterDrops"], DropsTypeToken.type) ?: emptyMap()
				
				TerrariaEnemy(name, type, globalDrops, normalDrops, expertDrops, masterDrops)
			}
	}
	
	companion object {
		private val comparator = compareBy(TerrariaEnemy::type, TerrariaEnemy::name)
	}
}
