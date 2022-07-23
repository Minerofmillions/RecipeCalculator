package minerofmillions.recipeapp.data.recipeentities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import minerofmillions.recipeapp.data.Rational
import minerofmillions.recipeapp.util.mapValuesNotNull
import minerofmillions.recipeapp.util.mergeOnto
import java.lang.reflect.Type

private typealias Drops = Map<Int, Map<String, Rational>>

class TerrariaLoot private constructor(
	private val _globalDrops: Drops,
	private val _normalDrops: Drops,
	private val _expertDrops: Drops,
	private val _masterDrops: Drops,
) {
	private val globalDrops = _globalDrops.withDefault { emptyMap() }
	private val normalDrops = _normalDrops.mergeOnto(globalDrops).toSortedMap()
	private val expertDrops = _expertDrops.mergeOnto(normalDrops).toSortedMap()
	private val masterDrops = _masterDrops.mergeOnto(expertDrops).toSortedMap()
	
	fun isNotEmpty() = masterDrops.isNotEmpty()
	fun <R> map(transform: (Map.Entry<Int, Map<String, Rational>>) -> R): List<R> = masterDrops.map(transform)
	fun <R> mapNotNull(transform: (Map.Entry<Int, Map<String, Rational>>) -> R?): List<R> = masterDrops.mapNotNull(transform)
	
	fun getMasterDrop(item: Int, condition: String = "") = masterDrops[item]?.get(condition) ?: masterDrops[item]?.get("") ?: Rational.ZERO
	fun getExpertDrop(item: Int, condition: String = "") = expertDrops[item]?.get(condition) ?: expertDrops[item]?.get("") ?: Rational.ZERO
	fun getNormalDrop(item: Int, condition: String = "") = normalDrops[item]?.get(condition) ?: normalDrops[item]?.get("") ?: Rational.ZERO
	
	fun hasMasterDrops(condition: String = "") = _masterDrops.any { it.value.containsKey(condition) }
	fun hasExpertDrops(condition: String = "") = _expertDrops.any { it.value.containsKey(condition) }
	fun hasNormalDrops(condition: String = "") = _normalDrops.any { it.value.containsKey(condition) } || _globalDrops.any { it.value.containsKey(condition) }
	
	fun getMasterDrops(condition: String = "") = masterDrops.mapValuesNotNull { (_, conditions) -> conditions[condition] }
	fun getExpertDrops(condition: String = "") = expertDrops.mapValuesNotNull { (_, conditions) -> conditions[condition] }
	fun getNormalDrops(condition: String = "") = normalDrops.mapValuesNotNull { (_, conditions) -> conditions[condition] }
	
	private object DropsTypeToken : TypeToken<Drops>()
	object Serializer : JsonDeserializer<TerrariaLoot> {
		override fun deserialize(
			json: JsonElement,
			typeOfT: Type,
			context: JsonDeserializationContext,
		): TerrariaLoot = json.asJsonObject.let {
			val globalDrops = context.deserialize<Drops>(it["globalLoot"], DropsTypeToken.type)!!
			val normalDrops = context.deserialize<Drops>(it["normalLoot"], DropsTypeToken.type) ?: emptyMap()
			val expertDrops = context.deserialize<Drops>(it["expertLoot"], DropsTypeToken.type) ?: emptyMap()
			val masterDrops = context.deserialize<Drops>(it["masterLoot"], DropsTypeToken.type) ?: emptyMap()
			
			TerrariaLoot(globalDrops, normalDrops, expertDrops, masterDrops)
		}
	}
}
