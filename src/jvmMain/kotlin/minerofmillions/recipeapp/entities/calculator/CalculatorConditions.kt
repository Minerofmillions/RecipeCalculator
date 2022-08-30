package minerofmillions.recipeapp.entities.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CalculatorConditions(val possibleConditions: List<String>) {
	var gameMode by mutableStateOf(GameMode.NORMAL)
	val activeConditions = mutableStateListOf("")
	
	fun toggleCondition(condition: String) {
		if (condition in activeConditions) {
			activeConditions.removeAll {
				it.split("&&").any(condition::equals)
			}
		} else {
			activeConditions.add(condition)
			activeConditions.addAll((possibleConditions - activeConditions).filter { it.split("&&").all(activeConditions::contains) })
		}
	}
	
	enum class GameMode {
		NORMAL, EXPERT, MASTER
	}
}
